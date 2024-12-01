package com.jero3000.appinstaller.domain

import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.test.runTest
import com.jero3000.appinstaller.repository.file.FileRepository
import kotlin.test.Test
import dev.mokkery.matcher.any
import dev.mokkery.verify
import dev.mokkery.verify.VerifyMode.Companion.atMost
import dev.mokkery.verify.VerifyMode.Companion.exactly
import dev.mokkery.verifySuspend
import dev.zwander.kotlin.file.IPlatformFile
import kotlin.test.assertTrue

class GetPackageFileUseCaseTest{

    @Test
    fun `get the package file when already downloaded`() = runTest{
        val fileRepository = mock<FileRepository>(){
            every { getFile(any()) } returns mock<IPlatformFile>()
        }

        val getFile = GetPackageFileUseCase(fileRepository)
        val file = getFile("https://www.example.com/file.apk")

        verify { fileRepository.getFile(any()) }
        verifySuspend(exactly(0)) { fileRepository.fetchFile(any()) }
        assertTrue { file.isSuccess }
    }

    @Test
    fun `get the package file when not downloaded`() = runTest{
        val fileRepository = mock<FileRepository>(){
            every { getFile(any()) } returns null
            everySuspend { fetchFile(any()) } returns Result.success(mock<IPlatformFile>())
        }

        val getFile = GetPackageFileUseCase(fileRepository)
        val file = getFile("https://www.example.com/file.apk")

        verify { fileRepository.getFile(any()) }
        verifySuspend(exactly(1)) { fileRepository.fetchFile(any()) }
        assertTrue { file.isSuccess }
    }
}