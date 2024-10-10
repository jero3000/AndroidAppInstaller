package org.example.project.appinstaller.domain

import org.example.project.appinstaller.model.AppConfig
import org.example.project.appinstaller.repository.ConfigurationRepository

class GetAppConfigUseCase(val repository: ConfigurationRepository) {
    suspend operator fun invoke() : Result<AppConfig> {
        return repository.getConfiguration()?.let { Result.success(it) } ?: repository.fetchConfiguration()
    }
}