package org.example.project.appinstaller.platform.filesystem

import dev.zwander.kotlin.file.IPlatformFile

class FileUtilsImpl(private val platformFileSystem: PlatformFileSystem): FileUtils {
    override fun getFileFromPath(path: String, isDirectory: Boolean): IPlatformFile? {
        return dev.zwander.kotlin.file.FileUtils.fromString(platformFileSystem.getUri(path), isDirectory)
    }
}