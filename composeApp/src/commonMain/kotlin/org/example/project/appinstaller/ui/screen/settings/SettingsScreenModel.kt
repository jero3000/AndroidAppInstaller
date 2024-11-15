package org.example.project.appinstaller.ui.screen.settings

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.launch
import org.example.project.appinstaller.domain.ClearCacheUseCase
import org.example.project.appinstaller.domain.ClearCredentialsUseCase
import org.example.project.appinstaller.ui.screen.settings.model.SettingsEvent

class SettingsScreenModel(private val clearCredentials: ClearCredentialsUseCase,
                          private val clearCache: ClearCacheUseCase): ScreenModel {

    fun onEvent(event: SettingsEvent) {
        when(event){
            SettingsEvent.OnClearCredentials -> {
                screenModelScope.launch {
                    clearCredentials()
                }
            }
            SettingsEvent.OnClearCache -> {
                screenModelScope.launch {
                    clearCache()
                }
            }
        }
    }
}
