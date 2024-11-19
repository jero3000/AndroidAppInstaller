package org.example.project.appinstaller.domain

import org.example.project.appinstaller.model.AppPackage
import org.example.project.appinstaller.platform.device.DeviceManager

class InstallAppPackageUseCase(private val deviceManager: DeviceManager) {
    suspend operator fun invoke(deviceSerial: String, appPackage: AppPackage): Result<Unit>{
        return deviceManager.getDevice(deviceSerial)?.let { device ->
            appPackage.packageFile?.takeIf { it.getExists() }?.let { _ ->
                device.install(appPackage)
            } ?: Result.failure(Exception("The package file to install is not available"))
        } ?: Result.failure(Exception("Device unavailable"))
    }
}
