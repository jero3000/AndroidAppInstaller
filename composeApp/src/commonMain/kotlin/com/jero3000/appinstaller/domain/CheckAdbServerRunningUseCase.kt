package com.jero3000.appinstaller.domain

import com.jero3000.appinstaller.model.Defaults
import com.jero3000.appinstaller.model.Settings
import com.jero3000.appinstaller.platform.adb.AdbBinary
import com.jero3000.appinstaller.repository.preferences.ApplicationPreferences

class CheckAdbServerRunningUseCase (
    private val preferences: ApplicationPreferences,
    private val adbBinary: AdbBinary) {

        suspend operator fun invoke(): Boolean {
            val host = preferences.getString(Settings.ADB_HOST.key) ?: Defaults.ADB_HOST
            val port = preferences.getInt(Settings.ADB_PORT.key) ?: Defaults.ADB_PORT
            return adbBinary.isServerRunning(host, port)
        }
    }
