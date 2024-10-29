package org.example.project.appinstaller.repository.credential

import org.example.project.appinstaller.model.Credential

class VolatileCredentialRepository: CredentialRepository {

    private val cache = mutableMapOf<String, Credential>()

    override suspend fun getCredential(host: String) = cache[host]

    override suspend fun putCredential(host: String, credential: Credential) {
        cache[host] = credential
    }
}
