package org.example.project.appinstaller.ui.screen.setup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.example.project.appinstaller.model.Credential
import org.example.project.appinstaller.ui.component.AppRow
import org.example.project.appinstaller.ui.component.CredentialsDialog
import org.example.project.appinstaller.ui.component.CustomAlertDialog
import org.example.project.appinstaller.ui.component.DropDownRow
import org.example.project.appinstaller.ui.component.VersionRow
import org.example.project.appinstaller.ui.component.rememberVersionState
import org.example.project.appinstaller.ui.screen.settings.SettingsScreen
import org.example.project.appinstaller.ui.screen.setup.model.SetupEvent
import org.example.project.appinstaller.ui.screen.setup.model.SetupPackage
import org.example.project.appinstaller.ui.screen.setup.model.SetupState
import org.example.project.appinstaller.model.AppVersion
import org.example.project.appinstaller.ui.theme.CustomTheme
import org.koin.compose.viewmodel.koinViewModel

class SetupScreen: Screen {

    @Composable
    override fun Content() {
        val viewModel = koinViewModel<SetupViewModel>()
        val navigator = LocalNavigator.currentOrThrow

        val uiState by viewModel.uiState.collectAsStateWithLifecycle(
            lifecycleOwner = LocalLifecycleOwner.current
        )

        if(!uiState.isLoading) {
            DisposableEffect(Unit) {
                viewModel.onEvent(SetupEvent.OnStart)
                onDispose {
                    viewModel.onEvent(SetupEvent.OnStop)
                }
            }

            if (uiState.projects.isEmpty()) {
                Column(
                    modifier = Modifier.padding(20.dp).fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center

                ) {
                    Text(
                        text = "Application configuration required. Please load a configuration file: File > load configuration...",
                        textAlign = TextAlign.Center,
                        fontSize = 24.sp
                    )
                }
            } else {
                Column(
                    modifier = Modifier.padding(20.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row {
                        DropDownRow(
                            modifier = Modifier.padding(end = 10.dp),
                            label = "Project",
                            options = uiState.projects,
                            default = uiState.selectedProject ?: "Not set"
                        ) { viewModel.onEvent(SetupEvent.OnProjectSelected(it)) }
                        DropDownRow(
                            label = "Target",
                            options = uiState.targets,
                            default = uiState.selectedTarget ?: "Not set"
                        ) { viewModel.onEvent(SetupEvent.OnTargetSelected(it)) }
                    }

                    val versionState = rememberVersionState(
                        uiState.selectedVersion?.major ?: "",
                        uiState.selectedVersion?.minor ?: "",
                        uiState.selectedVersion?.micro ?: "",
                        uiState.selectedVersion?.build ?: ""
                    )
                    VersionRow(modifier = Modifier.padding(top = 20.dp), versionState) {
                        viewModel.onEvent(SetupEvent.OnVersionEntered(
                            AppVersion(
                                versionState.major,
                                versionState.minor,
                                versionState.micro,
                                versionState.build.takeIf { it.isNotBlank() }
                            )
                        ))
                    }

                    DropDownRow(
                        modifier = Modifier.padding(top = 20.dp),
                        label = "Device",
                        options = uiState.devices.map { it.label },
                        default = uiState.selectedDevice?.label ?: "Not set"
                    ) { labelSelected ->
                        uiState.devices.firstOrNull { it.label == labelSelected }?.let {
                            viewModel.onEvent(SetupEvent.OnDeviceSelected(it))
                        }
                    }

                    Row(modifier = Modifier.padding(top = 20.dp, bottom = 14.dp)) {
                        Button(
                            colors = ButtonDefaults.buttonColors(),
                            onClick = { viewModel.onEvent(SetupEvent.OnDownloadClicked) },
                            enabled = uiState.selectedProject != null
                                    && uiState.selectedTarget != null
                                    && versionState.versionValid
                                    && uiState.selectedDevice != null
                        ) {
                            Text("Download")
                        }
                        Button(
                            modifier = Modifier.padding(start = 10.dp),
                            colors = ButtonDefaults.buttonColors(),
                            onClick = { viewModel.onEvent(SetupEvent.OnInstall) },
                            enabled = uiState.selectedDevice != null && uiState.packages.filter { it.selected }
                                .all { it.state == SetupPackage.State.Downloaded }
                        ) {
                            Text("Install")
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(5.dp))
                            .background(Color.LightGray)
                            .padding(6.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Application packages",
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                    if (uiState.selectedProject != null && uiState.selectedTarget != null) {
                        LazyColumn(
                            modifier = Modifier.wrapContentSize().padding(top = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            content = {
                                items(uiState.packages) { appPackage ->
                                    AppRow(modifier = Modifier.padding(top = 10.dp).width(450.dp),
                                        appName = appPackage.name,
                                        color = getAppColor(appPackage.state),
                                        checked = appPackage.selected,
                                        state = getAppState(appPackage.state),
                                        isTransient = isTransientState(appPackage.state),
                                        onCheckedChanged = { checked ->
                                            viewModel.onEvent(
                                                SetupEvent.OnSetupPackageChanged(
                                                    appPackage.packageName,
                                                    checked
                                                )
                                            )
                                        }
                                    )
                                }
                            }
                        )
                    } else {
                        Text(
                            modifier = Modifier.padding(top = 20.dp),
                            text = "You must select at least a project and a target in order to configure the application packages"
                        )
                    }
                }
            }
        }

        uiState.error?.let {
            when (it) {
                is SetupState.Error.CredentialsRequired -> {
                    CredentialsDialog(host = it.host,
                        onConfirmation = { user, pass ->
                            viewModel.onEvent(SetupEvent.OnErrorAck)
                            viewModel.onEvent(
                                SetupEvent.OnNewCredential(
                                    it.host,
                                    Credential(user, pass)
                                )
                            )
                        },
                        onCancel = { viewModel.onEvent(SetupEvent.OnErrorAck) })
                }

                is SetupState.Error.GenericError -> {
                    CustomAlertDialog("An error has occurred", it.description, "Ok", {}) {
                        viewModel.onEvent(SetupEvent.OnErrorAck)
                    }
                }

                SetupState.Error.AdbBinaryNotFound -> {
                    val annotatedString = buildAnnotatedString {
                        append("Please install the ")
                        withStyle(style = SpanStyle(color = Color.Blue, textDecoration = TextDecoration.Underline)) {
                            pushStringAnnotation(tag = "URL", annotation = "https://developer.android.com/studio/releases/platform-tools")
                            append("ADB tool")
                            pop()
                        }
                        append(" or configure the correct ADB path in the settings")
                    }


                    CustomAlertDialog(
                        "Android Debug Bridge (adb) executable not found",
                        text = {
                            ClickableText(
                                text = annotatedString,
                                onClick = { offset ->
                                    annotatedString.getStringAnnotations("URL", offset, offset).firstOrNull()?.let { annotation ->
                                        // Handle the click, open the URL
                                        viewModel.onEvent(SetupEvent.OnLinkClicked(annotation.item))
                                    }
                                }
                            )
                        },
                        "Go to settings", {}) {
                        viewModel.onEvent(SetupEvent.OnErrorAck)
                        navigator.push(SettingsScreen())
                    }
                }
            }
        }
    }


    @Composable
    private fun getAppColor(state: SetupPackage.State) = when (state) {
        SetupPackage.State.Idle -> CustomTheme.colors.idle
        SetupPackage.State.Downloading -> CustomTheme.colors.warning
        SetupPackage.State.Downloaded -> CustomTheme.colors.success
        SetupPackage.State.Installing -> CustomTheme.colors.warning
        SetupPackage.State.Installed -> CustomTheme.colors.success
        SetupPackage.State.Error -> CustomTheme.colors.error
    }

    private fun getAppState(state: SetupPackage.State) = when (state) {
        SetupPackage.State.Idle -> "Idle"
        SetupPackage.State.Downloading -> "Downloading..."
        SetupPackage.State.Downloaded -> "Downloaded"
        SetupPackage.State.Installing -> "Installing..."
        SetupPackage.State.Installed -> "Installed"
        SetupPackage.State.Error -> "Error"
    }

    private fun isTransientState(state: SetupPackage.State) = when (state) {
        SetupPackage.State.Idle -> false
        SetupPackage.State.Downloading -> true
        SetupPackage.State.Downloaded -> false
        SetupPackage.State.Installing -> true
        SetupPackage.State.Installed -> false
        SetupPackage.State.Error -> false
    }
}