package org.example.project.appinstaller.domain

import org.example.project.appinstaller.model.AppConfig
import org.example.project.appinstaller.repository.config.ConfigurationRepository

class GetAppConfigUseCase(val repository: ConfigurationRepository) {
    operator fun invoke() : AppConfig? {
        return repository.getConfiguration()
    }
}