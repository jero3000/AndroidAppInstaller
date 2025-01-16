package com.jero3000.appinstaller.platform.adb

import dev.zwander.kotlin.file.IPlatformFile
import dev.zwander.kotlin.file.PlatformFile
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.io.files.FileNotFoundException
import net.harawata.appdirs.AppDirsFactory
import java.io.File
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import java.util.Locale
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration.Companion.milliseconds

class AdbBinaryImpl(private val ioContext: CoroutineContext) : AdbBinary{

    override suspend fun searchForBinary(): Result<IPlatformFile> {
        return findViaWhich().takeIf { it.isSuccess }
            ?: findViaAndroidHome().takeIf { it.getOrNull() != null }
            ?: findViaPaths()
    }

    private fun findViaPaths(): Result<IPlatformFile> = runCatching {
        val appDirs = AppDirsFactory.getInstance()
        val adbName = if (isWindows()) "adb.exe" else "adb"
        val adbFile = File(appDirs.getUserDataDir(null, null, null))
            .resolve("Android")
            .resolve("Sdk")
            .resolve("platform-tools")
            .resolve(adbName).takeIf { it.exists() } ?:
        File(appDirs.getUserDataDir(null, null, null))
            .resolve("..")
            .resolve("Android")
            .resolve("sdk")
            .resolve("platform-tools")
            .resolve(adbName).takeIf { it.exists() } ?: throw FileNotFoundException("Adb binary cannot be found via filesystem paths")
        PlatformFile(adbFile)
    }

    private suspend fun findViaWhich(): Result<IPlatformFile> = runCatching {
        val which = if (isWindows()) "where" else "which"
        val output = withContext(ioContext) {
            val process = ProcessBuilder(which, "adb").start()
            if (process.waitFor() != 0) throw FileNotFoundException("Adb binary cannot be found on system path")
            process.inputStream.bufferedReader().use { r ->
                r.readLine().trim()
            }
        }

        val file = File(output)
        if (!file.exists()) throw FileNotFoundException("Adb binary cannot be found on system path")
        PlatformFile(file)
    }

    private fun findViaAndroidHome(): Result<IPlatformFile> = runCatching {
        val androidEnvHome = System.getenv("ANDROID_HOME") ?: System.getenv("ANDROID_SDK_ROOT") ?: throw FileNotFoundException("Adb binary cannot be found via Android env variables")
        val adbName = if (isWindows()) "adb.exe" else "adb"
        val adbFile = File(androidEnvHome).resolve("platform-tools").resolve(adbName)
        if (!adbFile.exists()) throw FileNotFoundException("Adb binary cannot be found via Android env variables")
        PlatformFile(adbFile)
    }

    private fun isWindows(): Boolean {
        return System.getProperty("os.name")?.lowercase(Locale.ENGLISH)?.contains("win") == true
    }

    override suspend fun isServerRunning(adbServerHost: String, adbServerPort: Int): Boolean {
        return try {
            withContext(ioContext) {
                Socket().apply {
                    connect(InetSocketAddress(adbServerHost, adbServerPort), 250)
                    close()
                }
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun startServer(adbBinary: IPlatformFile, adbServerPort: Int) = withContext(ioContext) {
        kotlin.runCatching {
            val process = ProcessBuilder(adbBinary.getAbsolutePath(), "-P", adbServerPort.toString(), "start-server")
             .redirectErrorStream(true)
             .start()
            val exited = process.waitFor(2, TimeUnit.SECONDS)
            if(exited){
                if (process.exitValue() != 0) {
                    val output = process.inputStream.bufferedReader().readText()
                    throw IOException("Failed to start adb server on port $adbServerPort: $output")
                }
            } else {
                throw TimeoutException("Failed to connect to adb server port $adbServerPort")
            }

        }.also {
            // Immediately after starting the adb server, emulators show as offline.
            // This is a hack to work around this behavior.
            delay(200.milliseconds)
        }
    }
}