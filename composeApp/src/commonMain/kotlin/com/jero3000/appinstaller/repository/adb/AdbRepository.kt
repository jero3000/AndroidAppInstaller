package com.jero3000.appinstaller.repository.adb

import dev.zwander.kotlin.file.IPlatformFile

interface AdbRepository {
    suspend fun getBinary(): Result<IPlatformFile>
    suspend fun putBinary(binary: IPlatformFile)
}