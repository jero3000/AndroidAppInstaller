package org.example.project.appinstaller.platform.filesystem

import kotlinx.io.IOException
import kotlinx.io.readString

class FileSystemImpl(private val platformFileSystem: PlatformFileSystem,
                     private val fileUtils: FileUtils) : FileSystem {

    override fun readConfiguration(fileName: String): Result<String> {
        val userHome = platformFileSystem.getUserDirectory()
        val separator = platformFileSystem.getFileSeparator()
        val configurationFilePath = "$userHome$separator.AndroidAppInstaller$separator$fileName"

        return readText(configurationFilePath)
    }

    private fun readText(path: String): Result<String> {
        return kotlin.runCatching {
            val uri = platformFileSystem.getUri(path)
            val file = fileUtils.getFileFromPath(uri, false) ?: throw IOException("File $path not found!")
            file.openInputStream()?.readString() ?: throw IOException("File $path cannot be opened!")
        }
    }
}
