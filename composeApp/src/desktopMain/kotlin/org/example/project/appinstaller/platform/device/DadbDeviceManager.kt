package org.example.project.appinstaller.platform.device

import dadb.Dadb
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class DadbDeviceManager(private val ioContext: CoroutineContext) : DeviceManager {

    private val availableDevices = mutableMapOf<String, Device>()
    private val mutex = Mutex()
    private val dadbMutex = Mutex()

    override suspend fun scan() = withContext(ioContext) {
        mutex.withLock {
            dadbMutex.withLock { Dadb.list() }.map { DadbDevice(it, ioContext, dadbMutex) }.forEach { device ->
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