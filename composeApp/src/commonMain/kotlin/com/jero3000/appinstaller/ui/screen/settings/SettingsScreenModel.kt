package com.jero3000.appinstaller.ui.screen.settings

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.jero3000.appinstaller.domain.CheckAdbServerRunningUseCase
import com.jero3000.appinstaller.domain.ClearCacheUseCase
import com.jero3000.appinstaller.domain.ClearCredentialsUseCase
import com.jero3000.appinstaller.domain.EnsureAdbServerRunningUseCase
import com.jero3000.appinstaller.domain.GetAdbBinaryUseCase
import com.jero3000.appinstaller.domain.PutAdbBinaryUseCase
import com.jero3000.appinstaller.model.Defaults
import com.jero3000.appinstaller.model.Settings
import com.jero3000.appinstaller.platform.device.Device
import com.jero3000.appinstaller.repository.preferences.ApplicationPreferences
import com.jero3000.appinstaller.ui.screen.settings.model.SettingsEvent

class SettingsScreenModel(private val clearCredentials: ClearCredentialsUseCase,
                          private val clearCache: ClearCacheUseCase,
                          private val preferences: ApplicationPreferences,
                          private val getAdbBinary: GetAdbBinaryUseCase,
                          private val putAdbBinary: PutAdbBinaryUseCase,
                          private val ensureAdbServerRunning: EnsureAdbServerRunningUseCase,
                          private val isAdbSeverRunningUseCase: CheckAdbServerRunningUseCase): StateScreenModel<SettingsScreenModel.State>(State()) {

    data class State(
        val isLoading: Boolean = true,
        val adbBinaryPath: String = "Adb binary cannot be found automatically",
        val installMode : Device.InstallMode = Device.InstallMode.NORMAL,
        val adbHost : String = Defaults.ADB_HOST,
        val adbPort : Int = Defaults.ADB_PORT,
        val adbServerRunning: Boolean = false
    )

    init {
        screenModelScope.launch {
            println("init")
            getAdbBinary().getOrNull()?.getAbsolutePath()?.let { path ->
                mutableState.update { it.copy(adbBinaryPath = path ) }
            }

            preferences.getString(Settings.INSTALL_MODE.key)?.let { mode ->
                mutableState.update { state -> state.copy(installMode = Device.InstallMode.entries.first { it.key == mode }) }
            }

            preferences.getString(Settings.ADB_HOST.key)?.let { host ->
                mutableState.update { it.copy(adbHost = host) }
            }

            preferences.getInt(Settings.ADB_PORT.key)?.let { port ->
                mutableState.update { it.copy(adbPort = port) }
            }

            mutableState.update { it.copy(adbServerRunning = isAdbSeverRunningUseCase(), isLoading = false) }
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
                    mutableState.update { it.copy(adbServerRunning = ensureAdbServerRunning().isSuccess) }
                }
            }
            is SettingsEvent.OnNewAdbHost -> {
                mutableState.update { it.copy(adbHost = event.host) }
                screenModelScope.launch {
                    preferences.putString(Settings.ADB_HOST.key, event.host)
                    mutableState.update { it.copy(adbServerRunning = isAdbSeverRunningUseCase()) }
                }
            }
            is SettingsEvent.OnNewAdbPort -> {
                mutableState.update { it.copy(adbPort = event.port) }
                screenModelScope.launch {
                    preferences.putInt(Settings.ADB_PORT.key, event.port)
                    mutableState.update { it.copy(adbServerRunning = isAdbSeverRunningUseCase()) }
                }
            }
        }
    }
}
