package com.jero3000.appinstaller.domain

import com.jero3000.appinstaller.model.AppPackage
import com.jero3000.appinstaller.platform.device.Device
import com.jero3000.appinstaller.platform.device.DeviceManager

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
