package org.example.project.appinstaller.repository.config.datasource

import org.example.project.appinstaller.model.AppConfig

interface ConfigurationDataSource {
    suspend fun getConfiguration(): Result<AppConfig>
}