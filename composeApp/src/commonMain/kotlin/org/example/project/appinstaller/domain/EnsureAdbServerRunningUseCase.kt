package org.example.project.appinstaller.domain

import org.example.project.appinstaller.model.Defaults
import org.example.project.appinstaller.model.Settings
import org.example.project.appinstaller.platform.adb.AdbBinary
import org.example.project.appinstaller.repository.adb.AdbRepository
import org.example.project.appinstaller.repository.preferences.ApplicationPreferences

class EnsureAdbServerRunningUseCase(
    private val preferences: ApplicationPreferences,
    private val adbRepository: AdbRepository,
    private val adbBinary: AdbBinary) {

    suspend operator fun invoke(): Result<Unit> {
        val host = preferences.getString(Settings.ADB_HOST.key) ?: Defaults.ADB_HOST
        val port = preferences.getInt(Settings.ADB_PORT.key) ?: Defaults.ADB_PORT
        return if(!adbBinary.isServerRunning(host, port) && host == Defaults.ADB_HOST){
            val binaryResult = adbRepository.getBinary()
            binaryResult.getOrNull()?.let { binary ->
                adbBinary.startServer(binary, port)
                Result.success(Unit)
            } ?: Result.failure(binaryResult.exceptionOrNull() ?: Exception("Unknown error"))
        } else {
            Result.success(Unit)
        }
    }
}
