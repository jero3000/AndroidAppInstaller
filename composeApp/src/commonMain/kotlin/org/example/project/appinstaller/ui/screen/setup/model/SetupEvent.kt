package org.example.project.appinstaller.ui.screen.setup.model

import org.example.project.appinstaller.model.Credential

sealed class SetupEvent{
    data class OnProjectSelected(val selected: String): SetupEvent()
    data class OnTargetSelected(val selected: String): SetupEvent()
    data class OnSetupPackageChanged(val packageName: String, val checked: Boolean): SetupEvent()
    data class OnDownloadClicked(val version: SetupVersion): SetupEvent()
    data object OnErrorAck: SetupEvent()
    data class OnNewCredential(val host: String, val credential: Credential): SetupEvent()
}
