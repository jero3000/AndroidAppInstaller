package org.example.project.appinstaller.repository.adb

import dev.zwander.kotlin.file.IPlatformFile

interface AdbRepository {
    suspend fun getBinary(): Result<IPlatformFile>
    suspend fun putBinary(binary: IPlatformFile)
}