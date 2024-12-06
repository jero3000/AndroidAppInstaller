package com.jero3000.appinstaller.domain

import com.jero3000.appinstaller.model.AppConfig
import com.jero3000.appinstaller.repository.config.ConfigurationRepository

class GetAppConfigUseCase(val repository: ConfigurationRepository) {
    suspend operator fun invoke() : AppConfig? {
        return repository.getConfiguration()
    }
}