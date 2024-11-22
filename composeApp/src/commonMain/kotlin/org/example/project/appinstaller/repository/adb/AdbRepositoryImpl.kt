package org.example.project.appinstaller.repository.adb

import dev.zwander.kotlin.file.IPlatformFile
import org.example.project.appinstaller.platform.adb.AdbBinary
import org.example.project.appinstaller.platform.filesystem.FileUtils
import org.example.project.appinstaller.platform.uri.UriParser
import org.example.project.appinstaller.repository.preferences.ApplicationPreferences

class AdbRepositoryImpl(private val preferences: ApplicationPreferences,
                        private val adbBinary: AdbBinary,
                        private val fileUtils: FileUtils) :  AdbRepository {

    private var binaryFile : IPlatformFile? = null

    override suspend fun getBinary(): Result<IPlatformFile> {
        return if(binaryFile != null){
            Result.success(binaryFile!!)
        } else {
            val result = preferences.getString(ADB_BINARY_PATH_SETTING)?.let { path ->
                fileUtils.getFileFromPath(path, false)?.takeIf { it.getExists() }
                    ?.let { Result.success(it) }
            } ?: adbBinary.searchForBinary()
            binaryFile = result.getOrNull()
            result
        }
    }

    override suspend fun putBinary(binary: IPlatformFile) {
        binaryFile = binary
        preferences.putString(ADB_BINARY_PATH_SETTING, binary.getAbsolutePath())
    }

    companion object{
        private const val ADB_BINARY_PATH_SETTING = "adb_binary"
    }
}