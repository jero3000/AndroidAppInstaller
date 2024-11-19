package org.example.project.appinstaller.platform.device

import org.example.project.appinstaller.model.AppPackage

interface Device {
    suspend fun getSerial(): Result<String>
    suspend fun getManufacturer(): Result<String>
    suspend fun getModel(): Result<String>
    suspend fun install(app: AppPackage): Result<Unit>
}
