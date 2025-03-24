package com.jero3000.appinstaller.repository.config.datasource

import dev.zwander.kotlin.file.IPlatformFile
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import com.jero3000.appinstaller.model.AppConfig
import com.jero3000.appinstaller.platform.filesystem.FileSystem
import com.jero3000.appinstaller.platform.filesystem.FileUtils
import com.jero3000.appinstaller.platform.filesystem.PlatformFileSystem
import com.jero3000.appinstaller.repository.config.json.AppConfigDto
import com.jero3000.appinstaller.repository.config.json.toAppConfig
import java.io.IOException
import kotlin.coroutines.CoroutineContext

class ConfigurationLocalDataSource(private val ioContext: CoroutineContext,
                                   private val fileSystem: FileSystem,
                                   private val platformFileSystem: PlatformFileSystem,
                                   private val fileUtils: FileUtils
) :
    ConfigurationDataSource {

    private val json = Json{ ignoreUnknownKeys = true }

    override suspend fun getConfiguration(): Result<AppConfig> {
        val appDataDirectory = platformFileSystem.getAppDataDirectory("AndroidAppInstaller", "jero3000")
        val filePath = platformFileSystem.combine(appDataDirectory, CONFIG_FILE_NAME)
        val file = fileUtils.getFileFromPath(filePath, false) ?: throw IOException("File $filePath not found!")
        return loadConfiguration(file, false)
    }

    override suspend fun loadConfiguration(file: IPlatformFile) = loadConfiguration(file, true)

    override suspend fun clearConfiguration() {
        val appDataDirectory = platformFileSystem.getAppDataDirectory("AndroidAppInstaller", "jero3000")
        val filePath = platformFileSystem.combine(appDataDirectory, CONFIG_FILE_NAME)
        fileUtils.getFileFromPath(filePath, false)?.delete()
    }

    private suspend fun loadConfiguration(file: IPlatformFile, copyToAppData: Boolean): Result<AppConfig> {
        val jsonResult = withContext(ioContext){
            fileSystem.readTextFile(file)
        }
        val result = jsonResult.getOrNull()?.let{
            runCatching {
                json.decodeFromString<AppConfigDto>(it).toAppConfig()
            }
        } ?: Result.failure(jsonResult.exceptionOrNull() ?: Exception("Unknown error parsing the JSON file"))
        if(result.isSuccess && copyToAppData){
            val appDataPath = platformFileSystem.getAppDataDirectory("AndroidAppInstaller", "jero3000")
            val appDataDirectory = fileUtils.getFileFromPath(appDataPath, true)
            val directoryOk = if(appDataDirectory?.getExists() == false) {
                appDataDirectory.mkdirs()
            } else appDataDirectory != null

            if(directoryOk){
                val targetPath = platformFileSystem.combine(appDataPath, CONFIG_FILE_NAME)
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
