package com.jero3000.appinstaller.platform.adb

import dev.zwander.kotlin.file.IPlatformFile

interface AdbBinary {
    suspend fun isServerRunning(adbServerHost: String, adbServerPort: Int): Boolean
    suspend fun searchForBinary(): Result<IPlatformFile>
    suspend fun startServer(adbBinary: IPlatformFile, adbServerPort: Int): Result<Unit>
}