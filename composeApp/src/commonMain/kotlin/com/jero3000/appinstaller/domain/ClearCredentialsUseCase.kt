package com.jero3000.appinstaller.domain

import com.jero3000.appinstaller.repository.credential.CredentialRepository

class ClearCredentialsUseCase(private val repository: CredentialRepository) {
    suspend operator fun invoke() {
        return repository.clear()
    }
}
