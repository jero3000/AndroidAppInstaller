package org.example.project.appinstaller.filesystem

interface FileSystem {
    fun readConfiguration(fileName: String): Result<String>
}

expect class FileSystemImpl