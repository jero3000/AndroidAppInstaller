package com.jero3000.appinstaller.repository.adb

import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import dev.zwander.kotlin.file.IPlatformFile
import kotlinx.coroutines.test.runTest
import com.jero3000.appinstaller.platform.adb.AdbBinary
import com.jero3000.appinstaller.platform.filesystem.FileUtils
import com.jero3000.appinstaller.repository.preferences.ApplicationPreferences
import org.junit.Test
import kotlin.test.assertNotNull
import kotlin.test.assertSame

class AdbRepositoryImplTest {

    @Test
    fun `Getting the adb binary checks for the user preference first`() = runTest {
        val preferences = mock<ApplicationPreferences>{
            everySuspend { getString(any()) } returns "/usr/bin/adb"
        }
        val adbBinary = mock<AdbBinary>()
        val fileUtils = mock<FileUtils>{
            every { getFileFromPath(any(), any()) } returns mock{
                every { getExists() } returns true
            }
        }
        val repository = AdbRepositoryImpl(preferences, adbBinary, fileUtils)

        val binary = repository.getBinary()
        verifySuspend { preferences.getString(any()) }
        verifySuspend(VerifyMode.not) { adbBinary.searchForBinary() }
        assertNotNull(binary)
    }

    @Test
    fun `Getting the adb binary searches for the binary if no user preference`() = runTest {
        val preferences = mock<ApplicationPreferences>{
            everySuspend { getString(any()) } returns null
        }
        val adbBinary = mock<AdbBinary>{
            everySuspend { searchForBinary() } returns Result.success(mock{
                every { getExists() } returns true
            })
        }
        val fileUtils = mock<FileUtils>()
        val repository = AdbRepositoryImpl(preferences, adbBinary, fileUtils)

        val binary = repository.getBinary()
        verifySuspend { preferences.getString(any()) }
        verifySuspend { adbBinary.searchForBinary() }
        assertNotNull(binary)
    }

    @Test
    fun `Setting the adb binary stores the file path as a preference`() = runTest {
        val preferences = mock<ApplicationPreferences>{
            everySuspend { putString(any(), any()) } returns Unit
        }
        val adbBinary = mock<AdbBinary>()
        val fileUtils = mock<FileUtils>()
        val repository = AdbRepositoryImpl(preferences, adbBinary, fileUtils)

        val fakeBinary = mock<IPlatformFile>{
            every { getAbsolutePath() } returns "/usr/bin/adb"
        }
        repository.putBinary(fakeBinary)
        verifySuspend { preferences.putString(any(), any()) }
        verifySuspend(VerifyMode.not) { adbBinary.searchForBinary() }

        val binary = repository.getBinary()
        assertSame(binary.getOrNull()!!, fakeBinary)
    }

}