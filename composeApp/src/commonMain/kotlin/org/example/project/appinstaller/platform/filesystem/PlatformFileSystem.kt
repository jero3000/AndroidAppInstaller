package org.example.project.appinstaller.platform.filesystem

interface PlatformFileSystem {
    fun getAppDataDirectory(): String
    fun getUserDirectory(): String
    fun getTempDirectory(): String
    fun getFileSeparator(): String
    fun getUri(path: String): String
}

expect class PlatformFileSystemImpl
