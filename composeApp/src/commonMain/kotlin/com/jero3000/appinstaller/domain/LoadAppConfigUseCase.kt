package com.jero3000.appinstaller.domain


import com.jero3000.appinstaller.platform.filesystem.FileUtils
import dev.zwander.kotlin.file.IPlatformFile
import com.jero3000.appinstaller.repository.config.ConfigurationRepository

class LoadAppConfigUseCase(private val repository: ConfigurationRepository,
                           private val fileUtils: FileUtils) {
    operator fun invoke(file: IPlatformFile) {
        repository.loadConfiguration(file)
    }

    operator fun invoke(path: String) {
        fileUtils.getFileFromPath(path, false)?.let {
            repository.loadConfiguration(it)
        }
    }
}