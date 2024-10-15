package org.example.project.appinstaller.repository.local

import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.example.project.appinstaller.platform.filesystem.FileSystem
import org.example.project.appinstaller.repository.config.datasource.ConfigurationLocalDataSource
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ConfigurationLocalDataSourceTest{

    private val testScope = TestScope()

    @Test
    fun `the configuration file in JSON format is properly parsed`() = testScope.runTest{
        val jsonResult = readResourceFile("data/app-data.json")
        val fileSystem = mock<FileSystem>{
            everySuspend { readConfiguration(any()) } returns jsonResult
        }
        val datasource = ConfigurationLocalDataSource(testScope.coroutineContext, fileSystem)

        val appConfigResult = datasource.getConfiguration()
        verify { fileSystem.readConfiguration(any()) }
        assertTrue(appConfigResult.isSuccess, appConfigResult.exceptionOrNull()?.message)
        val appConfig = appConfigResult.getOrNull()!!
        assertEquals(1,  appConfig.projects.size)
        assertEquals("project", appConfig.projects.first().name)
        assertEquals(2, appConfig.projects.first().buildVariants.size)
        assertEquals(2, appConfig.projects.first().buildVariants.first().packages.size)

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