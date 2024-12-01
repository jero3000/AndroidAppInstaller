package com.jero3000.appinstaller.domain

import dev.zwander.kotlin.file.IPlatformFile
import com.jero3000.appinstaller.repository.file.FileRepository

class GetPackageFileUseCase(private val fileRepository: FileRepository) {

    suspend operator fun invoke(url: String): Result<IPlatformFile>{
        return fileRepository.getFile(url)?.let { Result.success(it) } ?: fileRepository.fetchFile(url)
    }
}