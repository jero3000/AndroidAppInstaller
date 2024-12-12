package com.jero3000.appinstaller.domain

import com.jero3000.appinstaller.model.Settings
import com.jero3000.appinstaller.platform.adb.AdbBinary
import com.jero3000.appinstaller.repository.adb.AdbRepository
import com.jero3000.appinstaller.repository.preferences.ApplicationPreferences
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.answering.returnsSuccess
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import org.junit.Test

class EnsureAdbServerRunningUseCaseTest {
    @Test
    fun `Adb server host and port are obtained from preferences`() = runTest{
        val preferences = mock<ApplicationPreferences>(MockMode.autofill)
        val adbRepository = mock<AdbRepository>()
        val adbBinary = mock<AdbBinary>(MockMode.autofill)
        val ensureServerRunning = EnsureAdbServerRunningUseCase(preferences, adbRepository, adbBinary)
        ensureServerRunning()
        verifySuspend { preferences.getString(Settings.ADB_HOST.key) }
        verifySuspend { preferences.getInt(Settings.ADB_PORT.key) }
    }

    @Test
    fun `Adb server connection is tested before try to start the server`() = runTest {
        val preferences = mock<ApplicationPreferences>(MockMode.autofill)
        val adbRepository = mock<AdbRepository>()
        val adbBinary = mock<AdbBinary>(MockMode.autofill)
        val ensureServerRunning = EnsureAdbServerRunningUseCase(preferences, adbRepository, adbBinary)
        ensureServerRunning()
        verifySuspend { adbBinary.isServerRunning(any(), any()) }
    }

    @Test
    fun `adb binary is obtained from repository and abd server is started if not running`() = runTest {
        val preferences = mock<ApplicationPreferences>(MockMode.autofill){
            everySuspend { getString(Settings.ADB_HOST.key) } returns "localhost"
        }
        val adbRepository = mock<AdbRepository>{
            everySuspend { getBinary() } returnsSuccess mock()
        }
        val adbBinary = mock<AdbBinary>(MockMode.autofill){
            everySuspend { isServerRunning(any(), any()) } returns false
        }
        val ensureServerRunning = EnsureAdbServerRunningUseCase(preferences, adbRepository, adbBinary)
        ensureServerRunning()
        verifySuspend { adbBinary.startServer(any(), any()) }
    }
}