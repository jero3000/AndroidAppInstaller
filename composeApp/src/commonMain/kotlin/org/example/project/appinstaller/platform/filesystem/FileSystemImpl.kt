package org.example.project.appinstaller.platform.filesystem

import dev.zwander.kotlin.file.IPlatformFile
import kotlinx.io.IOException
import kotlinx.io.readString
import kotlinx.io.writeString

class FileSystemImpl(private val fileUtils: FileUtils) : FileSystem {

    override fun readTextFile(file: IPlatformFile): Result<String> {
        return kotlin.runCatching {
            file.openInputStream()?.readString()
                ?: throw IOException("File ${file.getPath()} cannot be opened!")
        }
    }

    override fun writeTextFile(data: String, file: IPlatformFile): Result<Unit> {
        return kotlin.runCatching {
            file.openOutputStream(false)?.writeString(data)
                ?: throw IOException("File ${file.getPath()} cannot be opened!")
        }
    }

    private fun readText(path: String): Result<String> {
        return kotlin.runCatching {
            val file = fileUtils.getFileFromPath(path, false) ?: throw IOException("File $path not found!")
            file.openInputStream()?.readString() ?: throw IOException("File $path cannot be opened!")
        }
    }
}
