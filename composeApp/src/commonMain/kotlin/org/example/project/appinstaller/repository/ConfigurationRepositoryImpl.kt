package org.example.project.appinstaller.repository

import org.example.project.appinstaller.model.AppConfig

class ConfigurationRepositoryImpl(private val dataSource: ConfigurationDataSource) : ConfigurationRepository {

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