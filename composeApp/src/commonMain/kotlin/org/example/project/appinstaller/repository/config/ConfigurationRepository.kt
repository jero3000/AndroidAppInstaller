package org.example.project.appinstaller.repository.config

import org.example.project.appinstaller.model.AppConfig


interface ConfigurationRepository {
    fun getConfiguration(): AppConfig?
    suspend fun fetchConfiguration(): Result<AppConfig>
}