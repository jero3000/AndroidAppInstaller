package com.jero3000.appinstaller.ui.screen.setup.model

import com.jero3000.appinstaller.model.AndroidDevice
import com.jero3000.appinstaller.model.AppVersion
import com.jero3000.appinstaller.model.Credential

sealed class SetupEvent{
    data object OnStart: SetupEvent()
    data object OnStop: SetupEvent()
    data class OnProjectSelected(val selected: String): SetupEvent()
    data class OnTargetSelected(val selected: String): SetupEvent()
    data class OnVersionEntered(val version: AppVersion): SetupEvent()
    data class OnDeviceSelected(val selected: AndroidDevice): SetupEvent()
    data class OnSetupPackageChanged(val packageName: String, val checked: Boolean): SetupEvent()
    data object OnDownloadClicked: SetupEvent()
    data object OnInstall: SetupEvent()
    data object OnErrorAck: SetupEvent()
    data class OnNewCredential(val host: String, val credential: Credential): SetupEvent()
    data class OnPlaceholderChanged(val id: String, val checked: Boolean): SetupEvent()
}
