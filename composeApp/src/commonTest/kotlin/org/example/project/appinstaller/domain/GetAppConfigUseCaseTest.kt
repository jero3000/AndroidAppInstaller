package org.example.project.appinstaller.domain

import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verify
import dev.mokkery.verify.VerifyMode.Companion.exactly
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import org.example.project.appinstaller.model.AppConfig
import org.example.project.appinstaller.repository.config.ConfigurationRepository
import kotlin.test.Test
import kotlin.test.assertTrue

class GetAppConfigUseCaseTest{
    @Test
    fun `get the configuration when already cached`() = runTest{
        val repository = mock<ConfigurationRepository>(){
            every { getConfiguration() } returns AppConfig(emptyList())
        }

        val getConfig = GetAppConfigUseCase(repository)
        val config = getConfig()

        verify { repository.getConfiguration() }
        verifySuspend(exactly(0)) { repository.fetchConfiguration() }
        assertTrue { config.isSuccess }
    }

    @Test
    fun `get the package file when not downloaded`() = runTest{
        val repository = mock<ConfigurationRepository>(){
            every { getConfiguration() } returns null
            everySuspend { fetchConfiguration() } returns Result.success(AppConfig(emptyList()))
        }

        val getConfig = GetAppConfigUseCase(repository)
        val config = getConfig()

        verify { repository.getConfiguration() }
        verifySuspend(exactly(1)) { repository.fetchConfiguration() }
        assertTrue { config.isSuccess }
    }
}