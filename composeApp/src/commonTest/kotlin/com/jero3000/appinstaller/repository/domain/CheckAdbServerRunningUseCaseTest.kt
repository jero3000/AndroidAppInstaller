package com.jero3000.appinstaller.repository.domain

import com.jero3000.appinstaller.domain.CheckAdbServerRunningUseCase
import com.jero3000.appinstaller.model.Settings
import com.jero3000.appinstaller.platform.adb.AdbBinary
import com.jero3000.appinstaller.repository.preferences.ApplicationPreferences
import dev.mokkery.MockMode.autofill
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import org.junit.Test

class CheckAdbServerRunningUseCaseTest {

    @Test
    fun `host and port must be retrieved from preferences when checking adb server`() = runTest {
        val preferences = mock<ApplicationPreferences>(autofill)
        val adbBinary = mock<AdbBinary>(autofill)
        val isServerRunning = CheckAdbServerRunningUseCase(preferences, adbBinary)
        isServerRunning()
        verifySuspend { preferences.getString(Settings.ADB_HOST.key) }
        verifySuspend { preferences.getInt(Settings.ADB_PORT.key) }
        verifySuspend { adbBinary.isServerRunning(any(), any()) }
    }
}