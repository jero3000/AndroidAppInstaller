package com.jero3000.appinstaller.repository.credential.datasource

import com.jero3000.appinstaller.model.Credential

class MemoryCredentialDataSource : CredentialDataSource {
    private val credentials = mutableMapOf<String, Credential>()

    override suspend fun getCredential(host: String): Credential? {
        return credentials[host]
    }

    override suspend fun putCredential(host: String, credential: Credential) {
        credentials[host] = credential
    }

    override suspend fun deleteCredential(host: String) {
        credentials.remove(host)
    }

    override suspend fun clear() {
        credentials.clear()
    }
}