package com.jero3000.appinstaller.repository.domain

import com.jero3000.appinstaller.domain.GetPackageFileUseCase
import com.jero3000.appinstaller.repository.file.FileRepository
import dev.mokkery.MockMode
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class GetPackageFileUseCaseTest {
    @Test
    fun `Tries to get the file from disk before fetch it from network`() = runTest{
        val repository = mock<FileRepository>(MockMode.autofill)
        val getFile = GetPackageFileUseCase(repository)
        getFile("http://www.example.com")
        verify { repository.getFile(any()) }
        verifySuspend { repository.fetchFile(any()) }
    }
}