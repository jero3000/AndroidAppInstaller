package org.example.project.appinstaller.domain

import org.example.project.appinstaller.repository.credential.CredentialRepository

class ClearCredentialsUseCase(private val repository: CredentialRepository) {
    suspend operator fun invoke() {
        return repository.clear()
    }
}
