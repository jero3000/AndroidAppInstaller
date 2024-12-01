package com.jero3000.appinstaller.domain

import com.jero3000.appinstaller.repository.file.FileRepository

class ClearCacheUseCase(private val fileRepository: FileRepository) {
    suspend operator fun invoke() {
        return fileRepository.clear()
    }
}
