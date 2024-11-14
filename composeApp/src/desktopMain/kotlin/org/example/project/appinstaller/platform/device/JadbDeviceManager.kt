package org.example.project.appinstaller.platform.device

import dadb.Dadb
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import se.vidstige.jadb.JadbConnection
import kotlin.coroutines.CoroutineContext

class JadbDeviceManager(private val ioContext: CoroutineContext) : DeviceManager {

    private val availableDevices = mutableMapOf<String, Device>()
    private val adbConnection = JadbConnection()
    private val mutex = Mutex()
    private val jadbMutex = Mutex()

    override suspend fun scan() = withContext(ioContext){
        mutex.withLock {
            jadbMutex.withLock { adbConnection.devices }.map { JadbDevice(it, ioContext, jadbMutex) }.forEach { device ->
                device.getSerial().getOrNull()?.let { serial ->
                    availableDevices[serial] = device
                }
            }
            availableDevices.values.toList()
        }
    }

    override suspend fun getDevice(serial: String): Device? {
        return mutex.withLock {
            availableDevices[serial]
        }
    }
}