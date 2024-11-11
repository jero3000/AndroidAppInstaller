package org.example.project.appinstaller.repository.config.datasource

import dev.zwander.kotlin.file.IPlatformFile
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.example.project.appinstaller.model.AppConfig
import org.example.project.appinstaller.platform.filesystem.FileSystem
import org.example.project.appinstaller.platform.filesystem.FileUtils
import org.example.project.appinstaller.platform.filesystem.PlatformFileSystem
import org.example.project.appinstaller.repository.config.json.AppConfigDto
import org.example.project.appinstaller.repository.config.json.toAppConfig
import java.io.IOException
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
        return loadConfiguration(file, false)
    }

    override suspend fun loadConfiguration(file: IPlatformFile) = loadConfiguration(file, true)

    private suspend fun loadConfiguration(file: IPlatformFile, copyToAppData: Boolean): Result<AppConfig> {
        val jsonResult = withContext(ioContext){
            fileSystem.readTextFile(file)
        }
        val result = jsonResult.getOrNull()?.let{
            runCatching {
                Json.decodeFromString<AppConfigDto>(it).toAppConfig()
            }
        } ?: Result.failure(jsonResult.exceptionOrNull() ?: Exception("Unknown error parsing the JSON file"))
        if(result.isSuccess && copyToAppData){
            val appDataDirectory = fileUtils.getFileFromPath(platformFileSystem.getAppDataDirectory(), true)
            val directoryOk = if(appDataDirectory?.getExists() == false) {
                appDataDirectory.mkdirs()
            } else appDataDirectory != null

            if(directoryOk){
                val targetPath = "${platformFileSystem.getAppDataDirectory()}${platformFileSystem.getFileSeparator()}$CONFIG_FILE_NAME"
                fileUtils.getFileFromPath(targetPath, false)?.let { targetFile ->
                    withContext(ioContext) {
                        fileSystem.writeTextFile(jsonResult.getOrNull()!!, targetFile)
                    }
                }
            }
        }
        return result
    }

    companion object{
        private const val CONFIG_FILE_NAME = "app-config.json"
    }
}
