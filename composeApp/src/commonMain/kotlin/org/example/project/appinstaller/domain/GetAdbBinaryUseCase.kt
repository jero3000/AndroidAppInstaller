package org.example.project.appinstaller.domain

import org.example.project.appinstaller.repository.adb.AdbRepository

class GetAdbBinaryUseCase(private val repository: AdbRepository) {
    suspend operator fun invoke() = repository.getBinary()
}