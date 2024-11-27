package org.example.project.appinstaller.domain

import org.example.project.appinstaller.model.Defaults
import org.example.project.appinstaller.model.Settings
import org.example.project.appinstaller.platform.adb.AdbBinary
import org.example.project.appinstaller.repository.preferences.ApplicationPreferences

class CheckAdbServerRunningUseCase (
    private val preferences: ApplicationPreferences,
    private val adbBinary: AdbBinary) {

        suspend operator fun invoke(): Boolean {
            val host = preferences.getString(Settings.ADB_HOST.key) ?: Defaults.ADB_HOST
            val port = preferences.getInt(Settings.ADB_PORT.key) ?: Defaults.ADB_PORT
            return adbBinary.isServerRunning(host, port)
        }
    }
