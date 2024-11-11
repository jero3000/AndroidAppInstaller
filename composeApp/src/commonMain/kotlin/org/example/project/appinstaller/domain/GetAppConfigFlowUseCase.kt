package org.example.project.appinstaller.domain

import kotlinx.coroutines.flow.Flow
import org.example.project.appinstaller.model.AppConfig
import org.example.project.appinstaller.repository.config.ConfigurationRepository

class GetAppConfigFlowUseCase(val repository: ConfigurationRepository) {
    operator fun invoke() : Flow<Result<AppConfig>> {
        return repository.getConfigurationFlow()
    }
}