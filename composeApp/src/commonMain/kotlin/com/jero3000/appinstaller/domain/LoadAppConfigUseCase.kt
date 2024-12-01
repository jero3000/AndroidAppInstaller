package com.jero3000.appinstaller.domain

import dev.zwander.kotlin.file.IPlatformFile
import com.jero3000.appinstaller.repository.config.ConfigurationRepository

class LoadAppConfigUseCase(val repository: ConfigurationRepository) {
    operator fun invoke(file: IPlatformFile) {
        return repository.loadConfiguration(file)
    }
}