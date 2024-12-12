package com.jero3000.appinstaller.repository.credential.datasource

import com.jero3000.appinstaller.model.Credential
import com.jero3000.appinstaller.repository.preferences.ApplicationPreferences
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.matcher.eq
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class PersistenceSwitcherTest {

    @Test
    fun `getCredential when save to disk is allowed`() = runTest{
        val memoryDataSource = mock<CredentialDataSource>(MockMode.autofill)
        val diskDataSource = mock<CredentialDataSource>(MockMode.autofill)
        val persistenceSwitcher = PersistenceSwitcher(
            memoryDataSource = memoryDataSource,
            diskDataSource = diskDataSource,
            preferences = mock<ApplicationPreferences>{
                everySuspend { getBoolean(any()) } returns true
            }
        )

        persistenceSwitcher.getCredential("host")
        verifySuspend { diskDataSource.getCredential(eq("host")) }
    }

    @Test
    fun `putCredential when save to disk is allowed`() = runTest{
        val memoryDataSource = mock<CredentialDataSource>(MockMode.autofill)
        val diskDataSource = mock<CredentialDataSource>(MockMode.autofill)
        val persistenceSwitcher = PersistenceSwitcher(
            memoryDataSource = memoryDataSource,
            diskDataSource = diskDataSource,
            preferences = mock<ApplicationPreferences>{
                everySuspend { getBoolean(any()) } returns true
            }
        )

        persistenceSwitcher.putCredential("host", Credential("user", "pass"))
        verifySuspend { diskDataSource.putCredential(eq("host"), eq(Credential("user", "pass"))) }
    }

    @Test
    fun `deleteCredential when save to disk is allowed`() = runTest{
        val memoryDataSource = mock<CredentialDataSource>(MockMode.autofill)
        val diskDataSource = mock<CredentialDataSource>(MockMode.autofill)
        val persistenceSwitcher = PersistenceSwitcher(
            memoryDataSource = memoryDataSource,
            diskDataSource = diskDataSource,
            preferences = mock<ApplicationPreferences>{
                everySuspend { getBoolean(any()) } returns true
            }
        )

        persistenceSwitcher.deleteCredential("host")
        verifySuspend { diskDataSource.deleteCredential(eq("host")) }
    }

    @Test
    fun `clear when save to disk is allowed`() = runTest{
        val memoryDataSource = mock<CredentialDataSource>(MockMode.autofill)
        val diskDataSource = mock<CredentialDataSource>(MockMode.autofill)
        val persistenceSwitcher = PersistenceSwitcher(
            memoryDataSource = memoryDataSource,
            diskDataSource = diskDataSource,
            preferences = mock<ApplicationPreferences>{
                everySuspend { getBoolean(any()) } returns true
            }
        )

        persistenceSwitcher.clear()
        verifySuspend { diskDataSource.clear() }
    }

    @Test
    fun `getCredential when save to disk is not allowed`() = runTest{
        val memoryDataSource = mock<CredentialDataSource>(MockMode.autofill)
        val diskDataSource = mock<CredentialDataSource>(MockMode.autofill)
        val persistenceSwitcher = PersistenceSwitcher(
            memoryDataSource = memoryDataSource,
            diskDataSource = diskDataSource,
            preferences = mock<ApplicationPreferences>{
                everySuspend { getBoolean(any()) } returns false
            }
        )

        persistenceSwitcher.getCredential("host")
        verifySuspend { memoryDataSource.getCredential(eq("host")) }
    }

    @Test
    fun `putCredential when save to disk is not allowed`() = runTest{
        val memoryDataSource = mock<CredentialDataSource>(MockMode.autofill)
        val diskDataSource = mock<CredentialDataSource>(MockMode.autofill)
        val persistenceSwitcher = PersistenceSwitcher(
            memoryDataSource = memoryDataSource,
            diskDataSource = diskDataSource,
            preferences = mock<ApplicationPreferences>{
                everySuspend { getBoolean(any()) } returns false
            }
        )

        persistenceSwitcher.putCredential("host", Credential("user", "pass"))
        verifySuspend { memoryDataSource.putCredential(eq("host"), eq(Credential("user", "pass"))) }
    }

    @Test
    fun `deleteCredential when save to disk is not allowed`() = runTest{
        val memoryDataSource = mock<CredentialDataSource>(MockMode.autofill)
        val diskDataSource = mock<CredentialDataSource>(MockMode.autofill)
        val persistenceSwitcher = PersistenceSwitcher(
            memoryDataSource = memoryDataSource,
            diskDataSource = diskDataSource,
            preferences = mock<ApplicationPreferences>{
                everySuspend { getBoolean(any()) } returns false
            }
        )

        persistenceSwitcher.deleteCredential("host")
        verifySuspend { memoryDataSource.deleteCredential(eq("host")) }
    }

    @Test
    fun `clear when save to disk is not allowed`() = runTest{
        val memoryDataSource = mock<CredentialDataSource>(MockMode.autofill)
        val diskDataSource = mock<CredentialDataSource>(MockMode.autofill)
        val persistenceSwitcher = PersistenceSwitcher(
            memoryDataSource = memoryDataSource,
            diskDataSource = diskDataSource,
            preferences = mock<ApplicationPreferences>{
                everySuspend { getBoolean(any()) } returns false
            }
        )

        persistenceSwitcher.clear()
        verifySuspend { memoryDataSource.clear() }
    }
}