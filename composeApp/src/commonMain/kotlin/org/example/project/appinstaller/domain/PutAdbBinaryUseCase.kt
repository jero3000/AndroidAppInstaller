package org.example.project.appinstaller.domain

import dev.zwander.kotlin.file.IPlatformFile
import org.example.project.appinstaller.repository.adb.AdbRepository

class PutAdbBinaryUseCase(private val repository: AdbRepository) {
    suspend operator fun invoke(file: IPlatformFile) = repository.putBinary(file)
}
