package com.jero3000.appinstaller.domain

import dev.zwander.kotlin.file.IPlatformFile
import com.jero3000.appinstaller.repository.adb.AdbRepository

class PutAdbBinaryUseCase(private val repository: AdbRepository) {
    suspend operator fun invoke(file: IPlatformFile) = repository.putBinary(file)
}
