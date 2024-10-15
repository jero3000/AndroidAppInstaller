package org.example.project.appinstaller.repository.config

import org.example.project.appinstaller.model.AppConfig
import org.example.project.appinstaller.repository.config.datasource.ConfigurationDataSource

class ConfigurationRepositoryImpl(private val dataSource: ConfigurationDataSource) :
    ConfigurationRepository {

    private var appConfig: AppConfig? = null

    override fun getConfiguration(): AppConfig? {
        return appConfig
    }

    override suspend fun fetchConfiguration(): Result<AppConfig> {
        return dataSource.getConfiguration().also { result  ->
            result.getOrNull()?.also {
                appConfig = it
            }
        }
    }
}