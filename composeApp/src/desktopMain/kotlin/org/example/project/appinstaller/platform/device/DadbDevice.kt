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

    override suspend fun install(app: AppPackage, mode: Device.InstallMode): Result<Unit> = withContext(ioContext){
        mutex.withLock {
            dadb.use{ device ->
                val result = when(mode){
                    Device.InstallMode.NORMAL -> runSecure(timeMillis = INSTALL_TIMEOUT_MS) {
                        device.install(File(app.packageFile!!.getPath()))
                    }
                    Device.InstallMode.DOWNGRADE -> runSecure(timeMillis = INSTALL_TIMEOUT_MS) {
                        device.install(File(app.packageFile!!.getPath()), "-d")
                    }
                    Device.InstallMode.CLEAN -> runSecure(timeMillis = INSTALL_TIMEOUT_MS) {
                        kotlin.runCatching {
                            device.uninstall(app.packageName)
                        }
                        device.install(File(app.packageFile!!.getPath()))
                    }
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
                val result = runSecure(timeMillis = GETPROP_TIMEOUT_MS){
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

    companion object{
        private const val INSTALL_TIMEOUT_MS = 20000L
        private const val GETPROP_TIMEOUT_MS = 5000L
    }
}
