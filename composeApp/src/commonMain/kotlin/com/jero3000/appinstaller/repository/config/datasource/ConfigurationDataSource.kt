package com.jero3000.appinstaller.repository.config.datasource

import dev.zwander.kotlin.file.IPlatformFile
import com.jero3000.appinstaller.model.AppConfig

interface ConfigurationDataSource {
    suspend fun getConfiguration(): Result<AppConfig>
    suspend fun loadConfiguration(file: IPlatformFile): Result<AppConfig>
    suspend fun clearConfiguration()
}