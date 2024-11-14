package org.example.project.appinstaller.platform.device

import dev.zwander.kotlin.file.IPlatformFile
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import se.vidstige.jadb.JadbDevice
import se.vidstige.jadb.Stream
import se.vidstige.jadb.managers.PackageManager
import java.io.File
import java.nio.charset.StandardCharsets
import kotlin.coroutines.CoroutineContext

class JadbDevice(private val jadb: JadbDevice, private val ioContext: CoroutineContext, private val mutex: Mutex) : Device {

    private val pm = PackageManager(jadb)

    override suspend fun getSerial() = kotlin.runCatching { jadb.serial }

    override suspend fun getManufacturer() = getDeviceProperty("ro.product.manufacturer")

    override suspend fun getModel() = getDeviceProperty("ro.product.model")

    override suspend fun install(app: IPlatformFile) = withContext(ioContext){
        mutex.withLock {
            runCatching {
                pm.install(File(app.getPath()))
            }
        }
    }

    private suspend fun getDeviceProperty(property: String) = withContext(ioContext){
        mutex.withLock {
            val result = kotlin.runCatching {
                val stream = jadb.executeShell("getprop", property)
                val output = Stream.readAll(stream, StandardCharsets.UTF_8)
                output.trimEnd()
            }
            if(result.isSuccess){
                Result.success(result.getOrNull()!!)
            } else {
                Result.failure(result.exceptionOrNull() ?: Exception("Unable to get the property $property"))
            }
        }
    }
}