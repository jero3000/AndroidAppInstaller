package org.example.project.appinstaller.domain

import dev.zwander.kotlin.file.IPlatformFile
import org.example.project.appinstaller.repository.config.ConfigurationRepository

class LoadAppConfigUseCase(val repository: ConfigurationRepository) {
    operator fun invoke(file: IPlatformFile) {
        return repository.loadConfiguration(file)
    }
}