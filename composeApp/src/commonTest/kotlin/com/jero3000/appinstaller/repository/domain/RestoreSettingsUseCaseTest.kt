package com.jero3000.appinstaller.repository.domain

import com.jero3000.appinstaller.domain.RestoreSettingsUseCase
import com.jero3000.appinstaller.repository.config.ConfigurationRepository
import com.jero3000.appinstaller.repository.preferences.ApplicationPreferences
import dev.mokkery.MockMode
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import org.junit.Test

class RestoreSettingsUseCaseTest {
    @Test
    fun `restore settings test case`() = runTest{
        val preferences = mock<ApplicationPreferences>(MockMode.autofill)
        val repository = mock<ConfigurationRepository>(MockMode.autofill)
        val restoreSettings = RestoreSettingsUseCase(preferences, repository)
        restoreSettings()
        verifySuspend { preferences.clear() }
        verifySuspend { repository.clearConfiguration() }
    }
}