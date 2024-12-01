package com.jero3000.appinstaller.repository.credential

import com.jero3000.appinstaller.model.Credential

interface CredentialRepository {
    suspend fun getCredential(host: String): Credential?
    suspend fun putCredential(host: String, credential: Credential)
    suspend fun deleteCredential(host: String)
    suspend fun clear()
}