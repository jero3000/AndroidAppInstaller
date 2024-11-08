package org.example.project.appinstaller.repository.credential

import org.example.project.appinstaller.model.Credential

interface CredentialRepository {
    suspend fun getCredential(host: String): Credential?
    suspend fun putCredential(host: String, credential: Credential)
    suspend fun deleteCredential(host: String)
    suspend fun clear()
}