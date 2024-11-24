package org.example.project.appinstaller.ui.screen.settings.model

import dev.zwander.kotlin.file.IPlatformFile
import org.example.project.appinstaller.platform.device.Device

sealed class SettingsEvent {
    data object OnClearCredentials : SettingsEvent()
    data object OnClearCache : SettingsEvent()
    data class OnInstallModeChanged(val mode: Device.InstallMode) : SettingsEvent()
    data class OnAdbBinaryConfigured(val binary: IPlatformFile) : SettingsEvent()
    data class OnNewAdbHost(val host: String) : SettingsEvent()
    data class OnNewAdbPort(val port: Int) : SettingsEvent()
}
