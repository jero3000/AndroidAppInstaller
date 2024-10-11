package org.example.project.appinstaller.ui.screen.setup

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.example.project.appinstaller.ui.component.AppRow
import org.example.project.appinstaller.ui.component.DropDownRow
import org.example.project.appinstaller.ui.component.VersionRow
import org.example.project.appinstaller.ui.component.rememberVersionState
import org.example.project.appinstaller.ui.screen.setup.model.SetupEvent
import org.example.project.appinstaller.ui.screen.setup.model.SetupPackage
import org.example.project.appinstaller.ui.screen.setup.model.SetupVersion
import org.example.project.appinstaller.ui.theme.CustomTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SetupScreen(viewModel : SetupViewModel = koinViewModel<SetupViewModel>()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle(
        lifecycleOwner = LocalLifecycleOwner.current
    )

    Column(modifier = Modifier.padding(20.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        DropDownRow(
            modifier = Modifier.padding(top = 20.dp),
            label = "Project",
            options = uiState.projects,
            default = uiState.selectedProject ?: "Not set"
        ){ viewModel.onEvent(SetupEvent.OnProjectSelected(it)) }
        DropDownRow(
            modifier = Modifier.padding(top = 20.dp),
            label = "Target",
            options = uiState.targets,
            default = uiState.selectedTarget ?: "Not set"
        ){ viewModel.onEvent(SetupEvent.OnTargetSelected(it)) }

        val versionState = rememberVersionState()
        VersionRow(modifier = Modifier.padding(top = 20.dp), versionState)

        Button(
            modifier = Modifier.padding(top = 20.dp),
            colors = ButtonDefaults.buttonColors(),
            onClick = {
                viewModel.onEvent(SetupEvent.OnDownloadClicked(SetupVersion(
                            versionState.major,
                            versionState.minor,
                            versionState.micro,
                            versionState.build)))
            }) {
            Text("Download")
        }
        Row {
            Column(modifier = Modifier.wrapContentSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                Row {
                    Text(modifier = Modifier.padding(top = 20.dp), text = "Apps:")
                }
                uiState.packages.forEach { appPackage ->
                    AppRow(modifier = Modifier.padding(top = 10.dp).width(450.dp),
                        appName = appPackage.name,
                        color = CustomTheme.colors.error,
                        checked = appPackage.selected,
                        state = getAppState(appPackage.state),
                        isTransient = isTransientState(appPackage.state),
                        onCheckedChanged = { checked ->
                            viewModel.onEvent(SetupEvent.OnSetupPackageChanged(appPackage.packageName, checked))
                        }
                    )
                }
            }
        }
    }
}

fun getAppState(state: SetupPackage.State) = when(state){
    SetupPackage.State.Idle -> "Idle"
    SetupPackage.State.Downloading -> "Downloading..."
    SetupPackage.State.Downloaded -> "Downloaded"
    SetupPackage.State.Installing -> "Installing..."
    SetupPackage.State.Installed -> "Installed"
}

fun isTransientState(state: SetupPackage.State) = when(state){
    SetupPackage.State.Idle -> false
    SetupPackage.State.Downloading -> true
    SetupPackage.State.Downloaded -> false
    SetupPackage.State.Installing -> true
    SetupPackage.State.Installed -> false
}
