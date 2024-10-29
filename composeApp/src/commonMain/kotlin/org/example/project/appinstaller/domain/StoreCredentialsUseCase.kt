package org.example.project.appinstaller.domain

import org.example.project.appinstaller.model.Credential
import org.example.project.appinstaller.repository.credential.CredentialRepository

class StoreCredentialsUseCase(private val repository: CredentialRepository) {
    suspend operator fun invoke(host: String, credential: Credential) {
        return repository.putCredential(host, credential)
    }
}
