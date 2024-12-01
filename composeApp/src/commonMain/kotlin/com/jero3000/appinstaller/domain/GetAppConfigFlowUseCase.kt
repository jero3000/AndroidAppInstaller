package com.jero3000.appinstaller.domain

import kotlinx.coroutines.flow.Flow
import com.jero3000.appinstaller.model.AppConfig
import com.jero3000.appinstaller.repository.config.ConfigurationRepository

class GetAppConfigFlowUseCase(val repository: ConfigurationRepository) {
    operator fun invoke() : Flow<Result<AppConfig>> {
        return repository.getConfigurationFlow()
    }
}