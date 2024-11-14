package org.example.project.appinstaller.platform.device

import dadb.Dadb
import dev.zwander.kotlin.file.IPlatformFile
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.coroutines.CoroutineContext

class DadbDevice(private val dadb: Dadb, private val ioContext: CoroutineContext, private val mutex: Mutex) : Device{

    override suspend fun getSerial() = getDeviceProperty("ro.serialno")

    override suspend fun getManufacturer() = getDeviceProperty("ro.product.manufacturer")

    override suspend fun getModel() = getDeviceProperty("ro.product.model")

    override suspend fun install(app: IPlatformFile) = withContext(ioContext){
        mutex.withLock {
            dadb.use{ device ->
                val result = kotlin.runCatching {
                    device.install(File(app.getPath()))
                }
                if(result.isSuccess){
                    Result.success(Unit)
                } else {
                    Result.failure(result.exceptionOrNull() ?: Exception("Error installing the package ${app.getName()}"))
                }
            }
        }
    }

    private suspend fun getDeviceProperty(property: String) = withContext(ioContext){
        mutex.withLock {
            dadb.use{ device ->
                val result = kotlin.runCatching {
                    device.shell("getprop $property")
                }
                if(result.isSuccess && result.getOrNull()!!.exitCode == 0 && result.getOrNull()!!.errorOutput.isEmpty()){
                    Result.success(result.getOrNull()!!.output.trimEnd())
                } else {
                    Result.failure(result.exceptionOrNull() ?: Exception(result.getOrNull()?.errorOutput))
                }
            }
        }
    }
}