package org.example.project.appinstaller.platform.device

import dadb.Dadb
import dev.zwander.kotlin.file.IPlatformFile
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.example.project.appinstaller.model.AppPackage
import org.example.project.appinstaller.utils.runSecure
import java.io.File
import kotlin.coroutines.CoroutineContext

class DadbDevice(private val dadb: Dadb, private val ioContext: CoroutineContext, private val mutex: Mutex) : Device{

    override suspend fun getSerial() = getDeviceProperty("ro.serialno")

    override suspend fun getManufacturer() = getDeviceProperty("ro.product.manufacturer")

    override suspend fun getModel() = getDeviceProperty("ro.product.model")

    override suspend fun install(app: AppPackage) = withContext(ioContext){
        mutex.withLock {
            dadb.use{ device ->
                val result = runSecure(timeMillis = 10000) {
                    device.install(File(app.packageFile!!.getPath()))
                }
                if(result.isSuccess){
                    Result.success(Unit)
                } else {
                    Result.failure(result.exceptionOrNull() ?: Exception("Error installing the package ${app.name}"))
                }
            }
        }
    }

    private suspend fun getDeviceProperty(property: String) = withContext(ioContext){
        mutex.withLock {
            dadb.use{ device ->
                val result = runSecure(timeMillis = 5000){
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
