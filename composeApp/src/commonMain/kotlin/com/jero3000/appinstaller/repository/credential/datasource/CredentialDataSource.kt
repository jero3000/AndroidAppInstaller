package com.jero3000.appinstaller.repository.credential.datasource

import com.jero3000.appinstaller.model.Credential

interface CredentialDataSource {
    suspend fun getCredential(host: String): Credential?
    suspend fun putCredential(host: String, credential: Credential)
    suspend fun deleteCredential(host: String)
    suspend fun clear()
}
