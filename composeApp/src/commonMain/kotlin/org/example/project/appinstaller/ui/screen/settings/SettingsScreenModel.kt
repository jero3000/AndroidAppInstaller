package org.example.project.appinstaller.ui.screen.settings

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.appinstaller.domain.ClearCacheUseCase
import org.example.project.appinstaller.domain.ClearCredentialsUseCase
import org.example.project.appinstaller.domain.GetAdbBinaryUseCase
import org.example.project.appinstaller.domain.PutAdbBinaryUseCase
import org.example.project.appinstaller.model.Defaults
import org.example.project.appinstaller.model.Settings
import org.example.project.appinstaller.platform.device.Device
import org.example.project.appinstaller.repository.preferences.ApplicationPreferences
import org.example.project.appinstaller.ui.screen.settings.model.SettingsEvent

class SettingsScreenModel(private val clearCredentials: ClearCredentialsUseCase,
                          private val clearCache: ClearCacheUseCase,
                          private val preferences: ApplicationPreferences,
                          private val getAdbBinary: GetAdbBinaryUseCase,
                          private val putAdbBinary: PutAdbBinaryUseCase): StateScreenModel<SettingsScreenModel.State>(State()) {

    data class State(
        val adbBinaryPath: String = "Adb binary cannot be found automatically",
        val installMode : Device.InstallMode = Device.InstallMode.NORMAL,
        val adbHost : String = "",
        val adbPort : String = ""
    )

    init {
        screenModelScope.launch {
            getAdbBinary().getOrNull()?.getAbsolutePath()?.let { path ->
                mutableState.update { it.copy(adbBinaryPath = path) }
            }
            preferences.getString(Settings.INSTALL_MODE.key)?.let { mode ->
                mutableState.update { state -> state.copy(installMode = Device.InstallMode.entries.first { it.key == mode }) }
            }
            val host = preferences.getString(Settings.ADB_HOST.key) ?: Defaults.ADB_HOST
            mutableState.update { it.copy(adbHost = host ) }

            val port = preferences.getInt(Settings.ADB_PORT.key) ?: Defaults.ADB_PORT
            mutableState.update { it.copy(adbPort = port.toString()) }
        }
    }

    fun onEvent(event: SettingsEvent) {
        when(event){
            SettingsEvent.OnClearCredentials -> screenModelScope.launch {
                clearCredentials()
            }
            SettingsEvent.OnClearCache -> screenModelScope.launch {
                clearCache()
            }
            is SettingsEvent.OnInstallModeChanged -> screenModelScope.launch {
                preferences.putString(Settings.INSTALL_MODE.key, event.mode.key)
                mutableState.update { state -> state.copy(installMode = event.mode) }
            }
            is SettingsEvent.OnAdbBinaryConfigured -> {
                mutableState.update { it.copy(adbBinaryPath = event.binary.getAbsolutePath()) }
                screenModelScope.launch {
                    putAdbBinary(event.binary)
                }
            }
            is SettingsEvent.OnNewAdbHost -> {
                mutableState.update { it.copy(adbHost = event.host) }
            }
            is SettingsEvent.OnNewAdbPort -> {
                mutableState.update { it.copy(adbPort = event.port) }
            }

            SettingsEvent.OnAdbHostConfirmed -> screenModelScope.launch {
                state.value.adbHost.takeIf { it.isNotBlank() }?.let {
                    preferences.putString(Settings.ADB_HOST.key, state.value.adbHost)
                }
            }
            SettingsEvent.OnAdbPortConfirmed -> screenModelScope.launch {
                state.value.adbPort.toIntOrNull()?.takeIf { it > 0 }?.let {
                    preferences.putInt(Settings.ADB_PORT.key, it)
                }
            }
        }
    }
}
