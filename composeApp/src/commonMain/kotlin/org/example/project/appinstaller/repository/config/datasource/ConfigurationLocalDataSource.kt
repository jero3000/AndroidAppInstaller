package org.example.project.appinstaller.repository.config.datasource

import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import net.harawata.appdirs.AppDirsFactory
import org.example.project.appinstaller.platform.filesystem.FileSystem
import org.example.project.appinstaller.model.AppConfig
import org.example.project.appinstaller.platform.filesystem.FileUtils
import org.example.project.appinstaller.platform.filesystem.PlatformFileSystem
import org.example.project.appinstaller.repository.config.json.AppConfigDto
import org.example.project.appinstaller.repository.config.json.toAppConfig
import java.io.IOException
import java.nio.file.Paths
import kotlin.coroutines.CoroutineContext

class ConfigurationLocalDataSource(private val ioContext: CoroutineContext,
                                   private val fileSystem: FileSystem,
                                   private val platformFileSystem: PlatformFileSystem,
                                   private val fileUtils: FileUtils
) :
    ConfigurationDataSource {
    override suspend fun getConfiguration(): Result<AppConfig> {
        val filePath = "${platformFileSystem.getAppDataDirectory()}${platformFileSystem.getFileSeparator()}$CONFIG_FILE_NAME"
        val file = fileUtils.getFileFromPath(filePath, false) ?: throw IOException("File $filePath not found!")
        val jsonResult = withContext(ioContext){
            fileSystem.readTextFile(file)
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
