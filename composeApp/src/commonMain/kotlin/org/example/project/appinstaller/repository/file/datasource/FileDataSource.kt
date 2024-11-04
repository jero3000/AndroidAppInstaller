package org.example.project.appinstaller.repository.file.datasource

import dev.zwander.kotlin.file.IPlatformFile
import org.example.project.appinstaller.model.Credential

interface FileDataSource {
    fun supports(url: String): Boolean

    suspend fun getFile(url: String, targetPath: String, credential: Credential?) : Result<IPlatformFile>
}