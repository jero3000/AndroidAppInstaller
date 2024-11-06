package org.example.project.appinstaller.repository.credential.datasource

import org.example.project.appinstaller.model.Credential

interface CredentialDataSource {
    suspend fun getCredential(host: String): Credential?
    suspend fun putCredential(host: String, credential: Credential)
    suspend fun deleteCredential(host: String)
}
