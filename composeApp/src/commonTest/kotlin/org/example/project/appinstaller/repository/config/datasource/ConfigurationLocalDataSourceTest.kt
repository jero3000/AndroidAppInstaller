package org.example.project.appinstaller.repository.config.datasource

import dev.mokkery.answering.returns
import dev.mokkery.answering.returnsSuccess
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.matcher.eqRef
import dev.mokkery.mock
import dev.mokkery.verify
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import dev.zwander.kotlin.file.IPlatformFile
import kotlinx.coroutines.test.runTest
import org.example.project.appinstaller.platform.filesystem.FileSystem
import org.example.project.appinstaller.platform.filesystem.FileUtils
import org.example.project.appinstaller.platform.filesystem.PlatformFileSystem
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ConfigurationLocalDataSourceTest{

    @Test
    fun `getConfiguration reads the config from user directory`() =  runTest {
        val jsonResult = readResourceFile("data/app-data.json")
        val fileSystem = mock<FileSystem>{
            everySuspend { readTextFile(any()) } returns jsonResult
        }
        val platformFileSystem : PlatformFileSystem = mock{
            every { getAppDataDirectory() } returns "/userDir"
            every { getFileSeparator() } returns "/"
        }
        val fileUtils : FileUtils = mock{
            every{ getFileFromPath(any(), any()) } returns mock()
        }
        val dataSource = ConfigurationLocalDataSource(backgroundScope.coroutineContext, fileSystem, platformFileSystem, fileUtils)

        val result = dataSource.getConfiguration()
        verify { platformFileSystem.getAppDataDirectory() }
        assertTrue(result.isSuccess)
        val appConfig = result.getOrNull()
        assertNotNull(appConfig)
        assertEquals(1,  appConfig.projects.size)
        assertEquals("project", appConfig.projects.first().name)
        assertEquals(2, appConfig.projects.first().buildVariants.size)
        assertEquals(1, appConfig.projects.first().buildVariants.first().deviceMap.size)
        assertEquals(2, appConfig.projects.first().buildVariants.first().packages.size)
    }

    @Test
    fun `loadConfiguration loads the configuration from a specific file and saves it to local directory`() = runTest {
        val jsonResult = readResourceFile("data/app-data.json")
        val fileSystem = mock<FileSystem>{
            everySuspend { readTextFile(any()) } returns jsonResult
            everySuspend { writeTextFile(any(), any()) } returnsSuccess Unit
        }
        val platformFileSystem : PlatformFileSystem = mock{
            every { getAppDataDirectory() } returns "/userDir"
            every { getFileSeparator() } returns "/"
        }
        val fileUtils : FileUtils = mock{
            every{ getFileFromPath(any(), any()) } returns mock(){
                every { getExists() } returns true
            }
        }
        val dataSource = ConfigurationLocalDataSource(backgroundScope.coroutineContext, fileSystem, platformFileSystem, fileUtils)

        val configFile = mock<IPlatformFile>()
        val result = dataSource.loadConfiguration(configFile)
        verifySuspend { fileSystem.readTextFile(eqRef(configFile)) }
        verify { platformFileSystem.getAppDataDirectory() }
        verifySuspend { fileSystem.writeTextFile(any(), any()) }
        assertTrue (result.isSuccess)
    }

    @Test
    fun `loadConfiguration loads the configuration from a specific file and skip saving it if the JSON is incorrect`() = runTest {
        val jsonResult = readResourceFile("data/app-data-wrong.json")
        val fileSystem = mock<FileSystem>{
            everySuspend { readTextFile(any()) } returns jsonResult
            everySuspend { writeTextFile(any(), any()) } returnsSuccess Unit
        }
        val platformFileSystem : PlatformFileSystem = mock{
            every { getAppDataDirectory() } returns "/userDir"
            every { getFileSeparator() } returns "/"
        }
        val fileUtils : FileUtils = mock{
            every{ getFileFromPath(any(), any()) } returns mock(){
                every { getExists() } returns true
            }
        }
        val dataSource = ConfigurationLocalDataSource(backgroundScope.coroutineContext, fileSystem, platformFileSystem, fileUtils)

        val configFile = mock<IPlatformFile>()
        val result = dataSource.loadConfiguration(configFile)
        verifySuspend { fileSystem.readTextFile(eqRef(configFile)) }
        verifySuspend(VerifyMode.not) { platformFileSystem.getAppDataDirectory() }
        verifySuspend(VerifyMode.not) { fileSystem.writeTextFile(any(), any()) }
        assertTrue (result.isFailure)
    }

    @Suppress("SameParameterValue")
    private fun readResourceFile(filename: String): Result<String> {
        // Common code to read a file from resources
        val stream = this::class.java.getResourceAsStream("/$filename")
        return kotlin.runCatching {
            stream!!.bufferedReader().use { it.readText() }
        }
    }
}