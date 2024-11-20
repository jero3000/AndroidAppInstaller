package org.example.project.appinstaller.domain

import org.example.project.appinstaller.model.AppPackage
import org.example.project.appinstaller.platform.device.Device
import org.example.project.appinstaller.platform.device.DeviceManager

class InstallAppPackageUseCase(private val deviceManager: DeviceManager) {
    suspend operator fun invoke(deviceSerial: String, appPackage: AppPackage, installMode: String?): Result<Unit>{
        return deviceManager.getDevice(deviceSerial)?.let { device ->
            appPackage.packageFile?.takeIf { it.getExists() }?.let { _ ->
                val mode =
                    installMode?.let { Device.InstallMode.entries.first { it.key == installMode } }
                        ?: Device.InstallMode.NORMAL
                device.install(appPackage, mode)
            } ?: Result.failure(Exception("The package file to install is not available"))
        } ?: Result.failure(Exception("Device unavailable"))
    }
}
