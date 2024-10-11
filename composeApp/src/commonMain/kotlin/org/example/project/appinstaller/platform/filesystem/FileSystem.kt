package org.example.project.appinstaller.platform.filesystem

interface FileSystem {
    fun readConfiguration(fileName: String): Result<String>
}
