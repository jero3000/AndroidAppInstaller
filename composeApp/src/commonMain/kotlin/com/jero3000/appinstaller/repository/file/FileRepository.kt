package com.jero3000.appinstaller.repository.file

import dev.zwander.kotlin.file.IPlatformFile

interface FileRepository {
    fun getFile(url: String): IPlatformFile?
    suspend fun fetchFile(url: String) : Result<IPlatformFile>
    suspend fun clear()
}