package com.jero3000.appinstaller.domain

import com.jero3000.appinstaller.model.AppConfig
import com.jero3000.appinstaller.repository.config.ConfigurationRepository

class FetchConfigurationUseCase(private val repository: ConfigurationRepository) {
    suspend operator fun invoke(): Result<AppConfig>{
        return repository.fetchConfiguration()
    }
}