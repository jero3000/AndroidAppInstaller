package com.jero3000.appinstaller.domain

import com.jero3000.appinstaller.repository.credential.CredentialRepository
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.test.runTest
import com.jero3000.appinstaller.repository.file.FileRepository
import com.jero3000.appinstaller.repository.preferences.ApplicationPreferences
import dev.mokkery.MockMode
import kotlin.test.Test
import dev.mokkery.matcher.any
import dev.mokkery.verify
import dev.mokkery.verify.VerifyMode.Companion.exactly
import dev.mokkery.verifySuspend
import dev.zwander.kotlin.file.IPlatformFile
import kotlin.test.assertTrue

class GetPackageFileUseCaseTest{

    @Test
    fun `get the package file when already downloaded`() = runTest{
        val fileRepository = mock<FileRepository>{
            every { getFile(any()) } returns mock<IPlatformFile>()
        }
        val credentials = mock<CredentialRepository>(MockMode.autofill)
        val preferences = mock<ApplicationPreferences>{
            everySuspend { getBoolean(any()) } returns true
        }

        val getFile = GetPackageFileUseCase(fileRepository, credentials, preferences)
        val file = getFile("https://www.example.com/file.apk")

        verify { fileRepository.getFile(any()) }
        verifySuspend(exactly(0)) { fileRepository.fetchFile(any()) }
        assertTrue { file.isSuccess }
    }

    @Test
    fun `get the package file when not downloaded`() = runTest{
        val fileRepository = mock<FileRepository>{
            every { getFile(any()) } returns null
            everySuspend { fetchFile(any()) } returns Result.success(mock<IPlatformFile>())
        }
        val credentials = mock<CredentialRepository>(MockMode.autofill)
        val preferences = mock<ApplicationPreferences>{
            everySuspend { getBoolean(any()) } returns true
        }

        val getFile = GetPackageFileUseCase(fileRepository, credentials, preferences)
        val file = getFile("https://www.example.com/file.apk")

        verify { fileRepository.getFile(any()) }
        verifySuspend(exactly(1)) { fileRepository.fetchFile(any()) }
        assertTrue { file.isSuccess }
    }

    @Test
    fun `ensure the credentials are cleared after the download if saving credentials is not allowed`() = runTest{
        val fileRepository = mock<FileRepository>{
            every { getFile(any()) } returns null
            everySuspend { fetchFile(any()) } returns Result.success(mock<IPlatformFile>())
        }
        val credentials = mock<CredentialRepository>(MockMode.autofill)
        val preferences = mock<ApplicationPreferences>{
            everySuspend { getBoolean(any()) } returns false
        }

        val getFile = GetPackageFileUseCase(fileRepository, credentials, preferences)
        val file = getFile("https://www.example.com/file.apk")

        verify { fileRepository.getFile(any()) }
        verifySuspend(exactly(1)) { fileRepository.fetchFile(any()) }
        assertTrue { file.isSuccess }
        verifySuspend { credentials.clear() }
    }
}