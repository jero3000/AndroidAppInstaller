package org.example.project.appinstaller.ui.screen.setup.model

sealed class SetupEvent{
    data class OnProjectSelected(val selected: String): SetupEvent()
    data class OnTargetSelected(val selected: String): SetupEvent()
    data class OnSetupPackageChanged(val packageName: String, val checked: Boolean): SetupEvent()
    data class OnDownloadClicked(val version: SetupVersion): SetupEvent()
}
