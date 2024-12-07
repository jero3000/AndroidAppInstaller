package com.jero3000.appinstaller.repository.config

import dev.zwander.kotlin.file.IPlatformFile
import kotlinx.coroutines.flow.Flow
import com.jero3000.appinstaller.model.AppConfig


interface ConfigurationRepository {
    fun getConfiguration(): AppConfig?
    suspend fun fetchConfiguration(): Result<AppConfig>
    fun getConfigurationFlow(): Flow<Result<AppConfig>>
    fun loadConfiguration(file: IPlatformFile)
    suspend fun clearConfiguration()
}