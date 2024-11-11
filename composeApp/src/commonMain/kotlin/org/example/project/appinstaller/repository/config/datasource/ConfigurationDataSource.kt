package org.example.project.appinstaller.repository.config.datasource

import dev.zwander.kotlin.file.IPlatformFile
import org.example.project.appinstaller.model.AppConfig

interface ConfigurationDataSource {
    suspend fun getConfiguration(): Result<AppConfig>
    suspend fun loadConfiguration(file: IPlatformFile): Result<AppConfig>
}