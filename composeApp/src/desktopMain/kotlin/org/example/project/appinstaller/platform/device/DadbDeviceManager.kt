package org.example.project.appinstaller.platform.device

import dadb.Dadb
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.example.project.appinstaller.utils.runSecure
import kotlin.coroutines.CoroutineContext

class DadbDeviceManager(private val ioContext: CoroutineContext) : DeviceManager {

    private val availableDevices = mutableMapOf<String, Device>()
    private val mutex = Mutex()
    private val dadbMutex = Mutex()

    override suspend fun scan() = withContext(ioContext) {
        mutex.withLock {
            val result = dadbMutex.withLock { runSecure(timeMillis = 5000) { Dadb.list() } }
            result.getOrNull()?.map { DadbDevice(it, ioContext, dadbMutex) }?.forEach { device ->
                device.getSerial().getOrNull()?.let { serial ->
                    availableDevices[serial] = device
                }
            }
            if(result.isFailure){
                result.exceptionOrNull()?.let { Result.failure(it) } ?: Result.failure(Exception("Unknown error discovering for devices"))
            } else {
                Result.success(availableDevices.values.toList())
            }
        }
    }

    override suspend fun getDevice(serial: String): Device? {
        return mutex.withLock {
            availableDevices[serial]
        }
    }
}