package org.example.project.appinstaller.platform.device

import dev.zwander.kotlin.file.IPlatformFile

interface Device {
    suspend fun getSerial(): Result<String>
    suspend fun getManufacturer(): Result<String>
    suspend fun getModel(): Result<String>
    suspend fun install(app: IPlatformFile): Result<Unit>
}
