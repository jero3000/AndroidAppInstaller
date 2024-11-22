package org.example.project.appinstaller.ui.screen.setup.model

import org.example.project.appinstaller.model.AndroidDevice
import org.example.project.appinstaller.model.Credential

sealed class SetupEvent{
    data object OnStart: SetupEvent()
    data object OnStop: SetupEvent()
    data class OnProjectSelected(val selected: String): SetupEvent()
    data class OnTargetSelected(val selected: String): SetupEvent()
    data class OnVersionEntered(val version: SetupVersion): SetupEvent()
    data class OnDeviceSelected(val selected: AndroidDevice): SetupEvent()
    data class OnSetupPackageChanged(val packageName: String, val checked: Boolean): SetupEvent()
    data object OnDownloadClicked: SetupEvent()
    data object OnInstall: SetupEvent()
    data object OnErrorAck: SetupEvent()
    data class OnNewCredential(val host: String, val credential: Credential): SetupEvent()
    data class OnLinkClicked(val link: String): SetupEvent()
}
