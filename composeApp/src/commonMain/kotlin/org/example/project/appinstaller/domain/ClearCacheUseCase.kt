package org.example.project.appinstaller.domain

import org.example.project.appinstaller.repository.file.FileRepository

class ClearCacheUseCase(private val fileRepository: FileRepository) {
    suspend operator fun invoke() {
        return fileRepository.clear()
    }
}
