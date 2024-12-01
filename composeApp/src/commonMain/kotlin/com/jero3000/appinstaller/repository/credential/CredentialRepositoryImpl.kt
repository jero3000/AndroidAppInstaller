package com.jero3000.appinstaller.repository.credential

import com.jero3000.appinstaller.model.Credential
import com.jero3000.appinstaller.repository.credential.datasource.CredentialDataSource

class CredentialRepositoryImpl(private val dataSource: CredentialDataSource): CredentialRepository {

    override suspend fun getCredential(host: String) = dataSource.getCredential(host)

    override suspend fun putCredential(host: String, credential: Credential) {
        dataSource.putCredential(host, credential)
    }

    override suspend fun deleteCredential(host: String) {
        dataSource.deleteCredential(host)
    }

    override suspend fun clear() {
        dataSource.clear()
    }
}
