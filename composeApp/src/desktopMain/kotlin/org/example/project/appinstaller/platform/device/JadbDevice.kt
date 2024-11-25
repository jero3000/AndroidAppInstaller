package org.example.project.appinstaller.platform.device

import dev.zwander.kotlin.file.IPlatformFile
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.example.project.appinstaller.model.AppPackage
import org.example.project.appinstaller.utils.runSecure
import se.vidstige.jadb.JadbDevice
import se.vidstige.jadb.Stream
import se.vidstige.jadb.managers.PackageManager
import se.vidstige.jadb.managers.PackageManager.ALLOW_VERSION_DOWNGRADE
import se.vidstige.jadb.managers.PackageManager.InstallOption
import java.io.File
import java.nio.charset.StandardCharsets
import kotlin.coroutines.CoroutineContext

class JadbDevice(private val jadb: JadbDevice, private val ioContext: CoroutineContext, private val mutex: Mutex) : Device {

    private val pm = PackageManager(jadb)

    override suspend fun getSerial() = kotlin.runCatching { jadb.serial }

    override suspend fun getManufacturer() = getDeviceProperty("ro.product.manufacturer")

    override suspend fun getModel() = getDeviceProperty("ro.product.model")

    override suspend fun install(app: AppPackage, mode: Device.InstallMode): Result<Unit> = withContext(ioContext){
        mutex.withLock {
            when(mode){
                Device.InstallMode.NORMAL -> runSecure(timeMillis = INSTALL_TIMEOUT_MS) {
                    pm.install(File(app.packageFile!!.getPath()))
                }
                Device.InstallMode.DOWNGRADE -> runSecure(timeMillis = INSTALL_TIMEOUT_MS) {
                    pm.installWithOptions(File(app.packageFile!!.getPath()), listOf(ALLOW_VERSION_DOWNGRADE))
                }
                Device.InstallMode.CLEAN -> runSecure(timeMillis = INSTALL_TIMEOUT_MS) {
                    kotlin.runCatching {
                        pm.uninstall(se.vidstige.jadb.managers.Package(app.packageName))
                    }.exceptionOrNull()?.let {
                        println("Error uninstalling ${app.name}: ${it.stackTraceToString()}")
                    }
                    pm.install(File(app.packageFile!!.getPath()))
                }
            }
        }
    }

    private suspend fun getDeviceProperty(property: String) = withContext(ioContext){
        mutex.withLock {
            val result = runSecure(timeMillis = GETPROP_TIMEOUT_MS) {
                jadb.executeShell("getprop", property).use { stream ->
                    val output = Stream.readAll(stream, StandardCharsets.UTF_8)
                    output.trimEnd()
                }
            }
            if(result.isSuccess){
                Result.success(result.getOrNull()!!)
            } else {
                Result.failure(result.exceptionOrNull() ?: Exception("Unable to get the property $property"))
            }
        }
    }

    companion object{
        private const val INSTALL_TIMEOUT_MS = 20000L
        private const val GETPROP_TIMEOUT_MS = 5000L
    }
}