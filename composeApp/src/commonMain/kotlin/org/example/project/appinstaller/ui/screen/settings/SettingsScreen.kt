package org.example.project.appinstaller.ui.screen.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.zwander.kotlin.file.filekit.toKmpFile
import io.github.vinceglb.filekit.core.FileKit
import io.github.vinceglb.filekit.core.PickerMode
import kotlinx.coroutines.launch
import org.example.project.appinstaller.platform.device.Device
import org.example.project.appinstaller.ui.component.AnimatedButton
import org.example.project.appinstaller.ui.component.FilePicker
import org.example.project.appinstaller.ui.component.RadioEntry
import org.example.project.appinstaller.ui.component.RadioGroup
import org.example.project.appinstaller.ui.screen.settings.model.SettingsEvent

class SettingsScreen : Screen{

    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<SettingsScreenModel>()
        val state by screenModel.state.collectAsState()

        val navigator = LocalNavigator.currentOrThrow
        Column {
            TextButton( modifier =  Modifier.padding(start = 5.dp), onClick = {
                navigator.popUntilRoot()
            }) {
                Text("< back")
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val coroutineScope = rememberCoroutineScope()
                FilePicker(modifier = Modifier.padding(top = 10.dp, bottom = 20.dp),
                    "Adb binary",
                    state.adbBinaryPath){
                    coroutineScope.launch {
                        FileKit.pickFile(
                            mode = PickerMode.Single,
                            title = "Select the Adb binary executable",
                        )?.toKmpFile()?.let {
                            screenModel.onEvent(SettingsEvent.OnAdbBinaryConfigured(it))
                        }
                    }
                }
                Row(modifier = Modifier.padding(bottom = 20.dp)) {
                    TextField(
                        modifier = Modifier.width(200.dp).onFocusChanged { state ->
                            if(!state.isFocused){
                                screenModel.onEvent(SettingsEvent.OnAdbHostConfirmed)
                            }
                        },
                        value = state.adbHost,
                        onValueChange = { screenModel.onEvent(SettingsEvent.OnNewAdbHost(it)) },
                        label = { Text("Adb server host", style = MaterialTheme.typography.labelSmall) },
                        isError = state.adbHost.isBlank(),
                        singleLine = true
                    )
                    TextField(
                        modifier = Modifier.width(200.dp).padding(start = 10.dp).onFocusChanged {
                            if(!it.isFocused){
                                screenModel.onEvent(SettingsEvent.OnAdbPortConfirmed)
                            }
                        },
                        value = state.adbPort,
                        onValueChange = { screenModel.onEvent(SettingsEvent.OnNewAdbPort(it)) },
                        label = { Text("Adb server port", style = MaterialTheme.typography.labelSmall) },
                        isError = state.adbPort.toIntOrNull()?.let { it <= 0 } ?: true,
                        singleLine = true
                    )
                }
                val options = provideInstallationModes()
                RadioGroup(modifier = Modifier.width(250.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(Color.LightGray),
                    title = "Install mode",
                    selectedOption = options.first{ it.key == state.installMode.key },
                    radioOptions = options
                ){ mode ->
                    val installMode = Device.InstallMode.entries.first{ it.key == mode.key }
                    screenModel.onEvent(SettingsEvent.OnInstallModeChanged(installMode))
                }
                Row(modifier = Modifier.padding(top = 20.dp)){
                    AnimatedButton(text = "Clear credentials") {
                        screenModel.onEvent(SettingsEvent.OnClearCredentials)
                    }
                    AnimatedButton(modifier = Modifier.padding(start = 10.dp), text = "Clear cache") {
                        screenModel.onEvent(SettingsEvent.OnClearCache)
                    }
                }
            }
        }
    }
}

fun provideInstallationModes() =
    Device.InstallMode.entries.map {
        when (it) {
            Device.InstallMode.NORMAL -> RadioEntry(
                "Normal",
                "Normal installation mode",
                Device.InstallMode.NORMAL.key
            )
            Device.InstallMode.DOWNGRADE -> RadioEntry(
                "Downgrade",
                "Allows version downgrade keeping the application data",
                Device.InstallMode.DOWNGRADE.key
            )
            Device.InstallMode.CLEAN -> RadioEntry(
                "Clean",
                "Uninstall the app and data if present, then performs a clean install",
                Device.InstallMode.CLEAN.key
            )
        }
    }
