package org.example.project.appinstaller.ui.screen.settings

import androidappinstaller.composeapp.generated.resources.Res
import androidappinstaller.composeapp.generated.resources.settings_adb_binary
import androidappinstaller.composeapp.generated.resources.settings_adb_binary_picker_button_title
import androidappinstaller.composeapp.generated.resources.settings_adb_binary_picker_title
import androidappinstaller.composeapp.generated.resources.settings_adb_host
import androidappinstaller.composeapp.generated.resources.settings_adb_port
import androidappinstaller.composeapp.generated.resources.settings_adb_sticker_fail
import androidappinstaller.composeapp.generated.resources.settings_adb_sticker_ok
import androidappinstaller.composeapp.generated.resources.settings_back_button
import androidappinstaller.composeapp.generated.resources.settings_clear_cache_button
import androidappinstaller.composeapp.generated.resources.settings_clear_credentials_button
import androidappinstaller.composeapp.generated.resources.settings_install_mode
import androidappinstaller.composeapp.generated.resources.settings_install_mode_clean
import androidappinstaller.composeapp.generated.resources.settings_install_mode_clean_tooltip
import androidappinstaller.composeapp.generated.resources.settings_install_mode_downgrade
import androidappinstaller.composeapp.generated.resources.settings_install_mode_downgrade_tooltip
import androidappinstaller.composeapp.generated.resources.settings_install_mode_normal
import androidappinstaller.composeapp.generated.resources.settings_install_mode_normal_tooltip
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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
import org.example.project.appinstaller.ui.component.StatusSticker
import org.example.project.appinstaller.ui.screen.settings.model.SettingsEvent
import org.jetbrains.compose.resources.stringResource

class SettingsScreen : Screen{

    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<SettingsScreenModel>()
        val state by screenModel.state.collectAsState()

        val navigator = LocalNavigator.currentOrThrow
        if(!state.isLoading) {
            Column {
                TextButton(modifier = Modifier.padding(start = 5.dp), onClick = {
                    navigator.popUntilRoot()
                }) {
                    Text("< " + stringResource(Res.string.settings_back_button))
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val coroutineScope = rememberCoroutineScope()
                    val title = stringResource(Res.string.settings_adb_binary_picker_title)
                    FilePicker(
                        modifier = Modifier.padding(top = 10.dp, bottom = 20.dp),
                        stringResource(Res.string.settings_adb_binary),
                        state.adbBinaryPath,
                        stringResource(Res.string.settings_adb_binary_picker_button_title)
                    ) {
                        coroutineScope.launch {
                            FileKit.pickFile(
                                mode = PickerMode.Single,
                                title = title,
                            )?.toKmpFile()?.let {
                                screenModel.onEvent(SettingsEvent.OnAdbBinaryConfigured(it))
                            }
                        }
                    }
                    Row(modifier = Modifier.padding(bottom = 20.dp), verticalAlignment = Alignment.CenterVertically) {
                        val (host, onHostChanged) = remember { mutableStateOf(state.adbHost) }
                        val (port, onPortChanged) = remember { mutableStateOf(state.adbPort.toString()) }
                        TextField(
                            modifier = Modifier.width(200.dp).onFocusChanged { state ->
                                if (!state.isFocused && host.isNotBlank()) {
                                    screenModel.onEvent(SettingsEvent.OnNewAdbHost(host))
                                }
                            },
                            value = host,
                            onValueChange = { onHostChanged(it) },
                            label = { Text(stringResource(Res.string.settings_adb_host), style = MaterialTheme.typography.labelSmall) },
                            isError = host.isBlank(),
                            singleLine = true
                        )
                        TextField(
                            modifier = Modifier.width(200.dp).padding(start = 10.dp)
                                .onFocusChanged {
                                    if (!it.isFocused) {
                                        port.toIntOrNull()?.let { portNumber ->
                                            screenModel.onEvent(SettingsEvent.OnNewAdbPort(portNumber))
                                        }
                                    }
                                },
                            value = port,
                            onValueChange = { onPortChanged(it) },
                            label = { Text(stringResource(Res.string.settings_adb_port), style = MaterialTheme.typography.labelSmall) },
                            isError = port.toIntOrNull()?.let { it <= 0 } ?: true,
                            singleLine = true
                        )
                        StatusSticker(modifier = Modifier.size(width = 155.dp, height = 57.dp).padding(start = 10.dp),
                            okStatusText = stringResource(Res.string.settings_adb_sticker_ok),
                            failStatusText = stringResource(Res.string.settings_adb_sticker_fail),
                            okStatus = state.adbServerRunning)
                    }
                    val options = provideInstallationModes()
                    RadioGroup(
                        modifier = Modifier.width(250.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(Color.LightGray),
                        title = stringResource(Res.string.settings_install_mode),
                        selectedOption = options.first { it.key == state.installMode.key },
                        radioOptions = options
                    ) { mode ->
                        val installMode = Device.InstallMode.entries.first { it.key == mode.key }
                        screenModel.onEvent(SettingsEvent.OnInstallModeChanged(installMode))
                    }
                    Row(modifier = Modifier.padding(top = 20.dp)) {
                        AnimatedButton(text = stringResource(Res.string.settings_clear_credentials_button)) {
                            screenModel.onEvent(SettingsEvent.OnClearCredentials)
                        }
                        AnimatedButton(
                            modifier = Modifier.padding(start = 10.dp),
                            text = stringResource(Res.string.settings_clear_cache_button)
                        ) {
                            screenModel.onEvent(SettingsEvent.OnClearCache)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun provideInstallationModes() =
    Device.InstallMode.entries.map {
        when (it) {
            Device.InstallMode.NORMAL -> RadioEntry(
                stringResource(Res.string.settings_install_mode_normal),
                stringResource(Res.string.settings_install_mode_normal_tooltip),
                Device.InstallMode.NORMAL.key
            )
            Device.InstallMode.DOWNGRADE -> RadioEntry(
                stringResource(Res.string.settings_install_mode_downgrade),
                stringResource(Res.string.settings_install_mode_downgrade_tooltip),
                Device.InstallMode.DOWNGRADE.key
            )
            Device.InstallMode.CLEAN -> RadioEntry(
                stringResource(Res.string.settings_install_mode_clean),
                stringResource(Res.string.settings_install_mode_clean_tooltip),
                Device.InstallMode.CLEAN.key
            )
        }
    }
