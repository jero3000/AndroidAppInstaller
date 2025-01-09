package com.jero3000.appinstaller.ui.screen.setup

import androidappinstaller.composeapp.generated.resources.Res
import androidappinstaller.composeapp.generated.resources.installer_icon
import androidappinstaller.composeapp.generated.resources.setup_adb_server_timeout
import androidappinstaller.composeapp.generated.resources.setup_adb_server_timeout_message
import androidappinstaller.composeapp.generated.resources.setup_app_packages
import androidappinstaller.composeapp.generated.resources.setup_config_required
import androidappinstaller.composeapp.generated.resources.setup_device
import androidappinstaller.composeapp.generated.resources.setup_download_button
import androidappinstaller.composeapp.generated.resources.setup_error_dialog_title
import androidappinstaller.composeapp.generated.resources.setup_install_button
import androidappinstaller.composeapp.generated.resources.setup_install_message1
import androidappinstaller.composeapp.generated.resources.setup_install_message2
import androidappinstaller.composeapp.generated.resources.setup_install_message3
import androidappinstaller.composeapp.generated.resources.setup_no_adb_message
import androidappinstaller.composeapp.generated.resources.setup_no_packages_warning
import androidappinstaller.composeapp.generated.resources.setup_not_set
import androidappinstaller.composeapp.generated.resources.setup_package_downloaded
import androidappinstaller.composeapp.generated.resources.setup_package_downloading
import androidappinstaller.composeapp.generated.resources.setup_package_error
import androidappinstaller.composeapp.generated.resources.setup_package_idle
import androidappinstaller.composeapp.generated.resources.setup_package_installed
import androidappinstaller.composeapp.generated.resources.setup_package_installing
import androidappinstaller.composeapp.generated.resources.setup_project
import androidappinstaller.composeapp.generated.resources.setup_settings_button
import androidappinstaller.composeapp.generated.resources.setup_target
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.jero3000.appinstaller.model.AppVersion
import com.jero3000.appinstaller.model.Credential
import com.jero3000.appinstaller.ui.component.AppRow
import com.jero3000.appinstaller.ui.component.CredentialsDialog
import com.jero3000.appinstaller.ui.component.CustomAlertDialog
import com.jero3000.appinstaller.ui.component.DropDownRow
import com.jero3000.appinstaller.ui.component.VersionRow
import com.jero3000.appinstaller.ui.component.rememberVersionState
import com.jero3000.appinstaller.ui.screen.settings.SettingsScreen
import com.jero3000.appinstaller.ui.screen.setup.model.SetupEvent
import com.jero3000.appinstaller.ui.screen.setup.model.SetupPackage
import com.jero3000.appinstaller.ui.screen.setup.model.SetupState
import com.jero3000.appinstaller.ui.theme.CustomTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
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
                    Image(
                        modifier = Modifier.size(256.dp),
                        painter = painterResource(Res.drawable.installer_icon),
                        contentDescription = null
                    )
                    Text(
                        modifier = Modifier.padding(top = 40.dp),
                        text = stringResource(Res.string.setup_config_required),
                        textAlign = TextAlign.Center,
                        fontSize = 24.sp,
                        style = TextStyle( lineHeight = 30.sp )
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
                            label = stringResource(Res.string.setup_project),
                            options = uiState.projects,
                            default = uiState.selectedProject ?: stringResource(Res.string.setup_not_set)
                        ) { viewModel.onEvent(SetupEvent.OnProjectSelected(it)) }
                        DropDownRow(
                            label = stringResource(Res.string.setup_target),
                            options = uiState.targets,
                            default = uiState.selectedTarget ?: stringResource(Res.string.setup_not_set)
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
                        label = stringResource(Res.string.setup_device),
                        options = uiState.devices.map { it.label },
                        default = uiState.selectedDevice?.label ?: stringResource(Res.string.setup_not_set)
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
                            Text(stringResource(Res.string.setup_download_button))
                        }
                        Button(
                            modifier = Modifier.padding(start = 10.dp),
                            colors = ButtonDefaults.buttonColors(),
                            onClick = { viewModel.onEvent(SetupEvent.OnInstall) },
                            enabled = uiState.selectedDevice != null && uiState.packages.filter { it.selected }
                                .all { it.state == SetupPackage.State.Downloaded }
                        ) {
                            Text(stringResource(Res.string.setup_install_button))
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
                            text = stringResource(Res.string.setup_app_packages),
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
                            text = stringResource(Res.string.setup_no_packages_warning)
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
                    CustomAlertDialog(stringResource(Res.string.setup_error_dialog_title), it.description, "Ok", {}) {
                        viewModel.onEvent(SetupEvent.OnErrorAck)
                    }
                }

                SetupState.Error.AdbBinaryNotFound -> {
                    CustomAlertDialog(
                        title = stringResource(Res.string.setup_no_adb_message),
                        text = {
                            Text(buildAnnotatedString {
                                append(stringResource(Res.string.setup_install_message1))
                                withLink(
                                    LinkAnnotation.Url(
                                        url = "https://developer.android.com/studio/releases/platform-tools",
                                        styles = TextLinkStyles(style = SpanStyle(
                                            color = MaterialTheme.colorScheme.primary,
                                            textDecoration = TextDecoration.Underline
                                        ))
                                    )
                                ) {
                                    append(stringResource(Res.string.setup_install_message2))
                                }
                                append(stringResource(Res.string.setup_install_message3))
                            })
                        },
                        buttonText = stringResource(Res.string.setup_settings_button), {}) {
                        viewModel.onEvent(SetupEvent.OnErrorAck)
                        navigator.push(SettingsScreen())
                    }
                }

                SetupState.Error.AdbServerTimeout -> {
                    CustomAlertDialog(title = stringResource(Res.string.setup_adb_server_timeout),
                        text = stringResource(Res.string.setup_adb_server_timeout_message),
                        "Ok", {}) {
                        viewModel.onEvent(SetupEvent.OnErrorAck)
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

    @Composable
    private fun getAppState(state: SetupPackage.State) = when (state) {
        SetupPackage.State.Idle -> stringResource(Res.string.setup_package_idle)
        SetupPackage.State.Downloading -> stringResource(Res.string.setup_package_downloading)
        SetupPackage.State.Downloaded -> stringResource(Res.string.setup_package_downloaded)
        SetupPackage.State.Installing -> stringResource(Res.string.setup_package_installing)
        SetupPackage.State.Installed -> stringResource(Res.string.setup_package_installed)
        SetupPackage.State.Error -> stringResource(Res.string.setup_package_error)
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