package org.example.project.appinstaller.platform.filesystem

import dev.zwander.kotlin.file.IPlatformFile

interface FileUtils {
    fun getFileFromPath(path: String, isDirectory: Boolean): IPlatformFile?
}