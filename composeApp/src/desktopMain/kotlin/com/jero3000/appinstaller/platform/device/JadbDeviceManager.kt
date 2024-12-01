package com.jero3000.appinstaller.platform.device

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import com.jero3000.appinstaller.utils.runSecure
import se.vidstige.jadb.JadbConnection
import kotlin.coroutines.CoroutineContext

class JadbDeviceManager(private val ioContext: CoroutineContext) : DeviceManager {

    private val availableDevices = mutableMapOf<String, Device>()
    private val adbConnection = JadbConnection()
    private val mutex = Mutex()
    private val jadbMutex = Mutex()

    override suspend fun scan() = withContext(ioContext){
        mutex.withLock {
            val result = jadbMutex.withLock { runSecure(timeMillis = 5000) { adbConnection.devices } }
            result.getOrNull()
                ?.filter { runSecure(timeMillis = 5000){ it.state }.getOrNull() == se.vidstige.jadb.JadbDevice.State.Device }
                ?.map { JadbDevice(it, ioContext, jadbMutex) }?.forEach { device ->
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