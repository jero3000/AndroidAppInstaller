package org.example.project.appinstaller.repository.config

import dev.zwander.kotlin.file.IPlatformFile
import kotlinx.coroutines.flow.Flow
import org.example.project.appinstaller.model.AppConfig


interface ConfigurationRepository {
    fun getConfiguration(): AppConfig?
    fun getConfigurationFlow(): Flow<Result<AppConfig>>
    fun loadConfiguration(file: IPlatformFile)
}