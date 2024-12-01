package com.jero3000.appinstaller.domain

import com.jero3000.appinstaller.model.Credential
import com.jero3000.appinstaller.repository.credential.CredentialRepository

class StoreCredentialsUseCase(private val repository: CredentialRepository) {
    suspend operator fun invoke(host: String, credential: Credential) {
        return repository.putCredential(host, credential)
    }
}
