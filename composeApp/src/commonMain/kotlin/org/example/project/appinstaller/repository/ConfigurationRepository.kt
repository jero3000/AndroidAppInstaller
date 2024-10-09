package org.example.project.appinstaller.repository

import org.example.project.appinstaller.model.AppConfig


interface ConfigurationRepository {
    suspend fun getConfiguration(): Result<AppConfig>
}