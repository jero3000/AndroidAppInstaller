package org.example.project.appinstaller.platform.filesystem

import dev.zwander.kotlin.file.IPlatformFile
import kotlinx.io.IOException
import kotlinx.io.readString
import kotlinx.io.writeString

class FileSystemImpl : FileSystem {

    override fun readTextFile(file: IPlatformFile): Result<String> {
        return kotlin.runCatching {
            file.openInputStream()?.use { it.readString() }
                ?: throw IOException("File ${file.getPath()} cannot be opened!")
        }
    }

    override fun writeTextFile(data: String, file: IPlatformFile): Result<Unit> {
        return kotlin.runCatching {
            file.openOutputStream(false)?.use {
                it.writeString(data)
                it.flush()
            } ?: throw IOException("File ${file.getPath()} cannot be opened!")
        }
    }
}
