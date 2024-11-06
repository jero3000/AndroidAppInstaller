package org.example.project.appinstaller.platform.filesystem

import dev.zwander.kotlin.file.IPlatformFile

class FileUtilsImpl: FileUtils {
    override fun getFileFromPath(path: String, isDirectory: Boolean): IPlatformFile? {
        return dev.zwander.kotlin.file.FileUtils.fromString(path, isDirectory)
    }
}