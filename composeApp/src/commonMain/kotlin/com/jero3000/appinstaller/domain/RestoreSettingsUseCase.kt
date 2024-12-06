package com.jero3000.appinstaller.domain

import com.jero3000.appinstaller.repository.config.ConfigurationRepository
import com.jero3000.appinstaller.repository.preferences.ApplicationPreferences

class RestoreSettingsUseCase(private val preferences: ApplicationPreferences,
                             private val configurationRepository: ConfigurationRepository) {
    suspend operator fun invoke(){
        preferences.clear()
        configurationRepository.clearConfiguration()
    }
}