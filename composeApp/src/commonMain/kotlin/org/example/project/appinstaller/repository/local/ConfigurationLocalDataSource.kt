package org.example.project.appinstaller.repository.local

import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.example.project.appinstaller.filesystem.FileSystem
import org.example.project.appinstaller.model.AppConfig
import org.example.project.appinstaller.repository.ConfigurationDataSource
import org.example.project.appinstaller.repository.json.AppConfigDto
import org.example.project.appinstaller.repository.json.toAppConfig
import kotlin.coroutines.CoroutineContext

class ConfigurationLocalDataSource(private val ioContext: CoroutineContext, private val fileSystem: FileSystem) : ConfigurationDataSource{
    override suspend fun getConfiguration(): Result<AppConfig> {
        val jsonResult = withContext(ioContext){
            fileSystem.readConfiguration(CONFIG_FILE_NAME)
        }
        return jsonResult.getOrNull()?.let{
            runCatching {
                Json.decodeFromString<AppConfigDto>(it).toAppConfig()
            }
        } ?: Result.failure(jsonResult.exceptionOrNull() ?: Exception("Unknown error parsing the JSON file"))
    }

    companion object{
        private const val CONFIG_FILE_NAME = "app-config.json"
    }
}
