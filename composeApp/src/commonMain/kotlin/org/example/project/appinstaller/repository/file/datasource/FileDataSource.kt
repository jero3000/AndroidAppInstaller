package org.example.project.appinstaller.repository.file.datasource

import dev.zwander.kotlin.file.IPlatformFile

interface FileDataSource {
    fun supports(url: String): Boolean

    suspend fun getFile(url: String, targetPath: String) : Result<IPlatformFile>
}