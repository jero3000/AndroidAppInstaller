package org.example.project.appinstaller.platform.filesystem

interface PlatformFileSystem {
    fun getUserDirectory(): String
    fun getTempDirectory(): String
    fun getFileSeparator(): String
    fun getUri(path: String): String
}

expect class PlatformFileSystemImpl
