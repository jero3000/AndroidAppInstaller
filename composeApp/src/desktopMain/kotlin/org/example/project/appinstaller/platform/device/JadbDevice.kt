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

    override suspend fun install(app: AppPackage) = withContext(ioContext){
        mutex.withLock {
            runSecure(timeMillis = 10000) {
                pm.uninstall(se.vidstige.jadb.managers.Package(app.packageName))
                pm.install(File(app.packageFile!!.getPath()))
                //pm.forceInstall(File(app.getPath()))
                //pm.installWithOptions(File(app.getPath()), listOf(ALLOW_VERSION_DOWNGRADE))
            }
        }
    }

    private suspend fun getDeviceProperty(property: String) = withContext(ioContext){
        mutex.withLock {
            val result = runSecure(timeMillis = 5000) {
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
}