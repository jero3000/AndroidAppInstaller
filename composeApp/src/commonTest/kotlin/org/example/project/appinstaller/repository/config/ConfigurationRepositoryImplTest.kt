package org.example.project.appinstaller.repository.config

import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.example.project.appinstaller.model.AppConfig
import org.example.project.appinstaller.repository.config.datasource.ConfigurationDataSource
import org.junit.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ConfigurationRepositoryImplTest {

    @Test
    fun `At startup there is no cached configuration`(){
        val dataSource = mock<ConfigurationDataSource>()
        val repository = ConfigurationRepositoryImpl(dataSource)

        assertNull(repository.getConfiguration())
    }

    @Test
    fun `First tries to load configuration from user directory`() = runTest {
        val dataSource = mock<ConfigurationDataSource>(){
            everySuspend { getConfiguration() } returns Result.success(AppConfig(emptyList()))
        }
        val repository = ConfigurationRepositoryImpl(dataSource)

        repository.getConfigurationFlow().first()
        verifySuspend { dataSource.getConfiguration() }
        assertNotNull(repository.getConfiguration())
    }

    @Test
    fun `If there is no configuration at user directory, the configuration is loaded when the user specifies the file to load`() = runTest {
        val dataSource = mock<ConfigurationDataSource> {
            everySuspend { getConfiguration() } returns Result.failure(Exception())
            everySuspend { loadConfiguration(any()) } returns Result.success(AppConfig(emptyList()))
        }
        val repository = ConfigurationRepositoryImpl(dataSource)

        val job = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            repository.getConfigurationFlow().first()
        }

        repository.loadConfiguration(mock())
        job.join()
        verifySuspend { dataSource.getConfiguration() }
        verifySuspend { dataSource.loadConfiguration(any()) }
        assertNotNull(repository.getConfiguration())
    }

    @Test
    fun `Cached configuration is not updated if the load was not success`() = runTest {
        val dataSource = mock<ConfigurationDataSource> {
            everySuspend { getConfiguration() } returns Result.success(AppConfig(emptyList()))
            everySuspend { loadConfiguration(any()) } returns Result.failure(Exception())
        }
        val repository = ConfigurationRepositoryImpl(dataSource)

        var result: Result<AppConfig>? = null
        val job = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            result = repository.getConfigurationFlow().drop(1).first()
        }

        repository.loadConfiguration(mock())
        job.join()
        verifySuspend { dataSource.getConfiguration() }
        verifySuspend { dataSource.loadConfiguration(any()) }
        assertNotNull(repository.getConfiguration())
        assertNotNull(result)
        assertTrue(result!!.isFailure)
    }
}
