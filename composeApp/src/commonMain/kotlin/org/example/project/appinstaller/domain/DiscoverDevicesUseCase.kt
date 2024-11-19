package org.example.project.appinstaller.domain

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import org.example.project.appinstaller.model.AndroidDevice
import org.example.project.appinstaller.platform.device.DeviceManager
import kotlin.time.Duration.Companion.seconds

class DiscoverDevicesUseCase(private val deviceManager: DeviceManager) {
    operator fun invoke() = flow {
        while(true){
            val scanResult = deviceManager.scan()
            val devices = scanResult.getOrNull()?.mapNotNull {
                val serial = it.getSerial().getOrNull()
                val manufacturer = it.getManufacturer().getOrNull()
                val model = it.getModel().getOrNull()
                if (serial != null && manufacturer != null && model != null) {
                    AndroidDevice(serial, "$manufacturer $model")
                } else null
            } ?: emptyList()
            scanResult.exceptionOrNull()?.let {
                println("An exception occurred scanning for devices: " + it.stackTraceToString())
            }
            emit(devices)
            delay(5.seconds) //Refresh every 5 seconds
        }
    }
}
