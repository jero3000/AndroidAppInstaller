package org.example.project.appinstaller.repository

import org.example.project.appinstaller.model.AppConfig

class ConfigurationRepositoryImpl(private val dataSource: ConfigurationDataSource) : ConfigurationRepository {
    override suspend fun getConfiguration(): Result<AppConfig> = dataSource.getConfiguration()
}