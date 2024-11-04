package org.example.project.appinstaller.repository.credential

import org.example.project.appinstaller.model.Credential
import org.example.project.appinstaller.repository.credential.datasource.CredentialDataSource

class CredentialRepositoryImpl(private val dataSource: CredentialDataSource): CredentialRepository {

    override suspend fun getCredential(host: String) = dataSource.getCredential(host)

    override suspend fun putCredential(host: String, credential: Credential) {
        dataSource.putCredential(host, credential)
    }
}
