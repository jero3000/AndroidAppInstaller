package com.jero3000.appinstaller.repository.file.datasource

import dev.zwander.kotlin.file.IPlatformFile
import com.jero3000.appinstaller.model.Credential

interface FileDataSource {
    fun supports(url: String): Boolean

    suspend fun getFile(url: String, targetPath: String, credential: Credential?) : Result<IPlatformFile>
}