package com.jero3000.appinstaller.domain

import com.jero3000.appinstaller.repository.adb.AdbRepository

class GetAdbBinaryUseCase(private val repository: AdbRepository) {
    suspend operator fun invoke() = repository.getBinary()
}