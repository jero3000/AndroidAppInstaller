package org.example.project.appinstaller.ui.screen.settings.model

sealed class SettingsEvent{
    data object OnClearCredentials: SettingsEvent()
}
