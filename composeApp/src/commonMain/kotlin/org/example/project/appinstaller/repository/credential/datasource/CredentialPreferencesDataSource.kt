package org.example.project.appinstaller.repository.credential.datasource

import com.russhwolf.settings.Settings
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.example.project.appinstaller.model.Credential
import org.example.project.appinstaller.repository.credential.json.CredentialDto
import org.example.project.appinstaller.repository.credential.json.toCredential
import org.example.project.appinstaller.repository.credential.json.toCredentialDto
import kotlin.coroutines.CoroutineContext

class CredentialPreferencesDataSource(settingsFactory: Settings.Factory, private val ioContext: CoroutineContext): CredentialDataSource {

    private val settings =  settingsFactory.create("org.example.project.appinstaller")

    override suspend fun getCredential(host: String) = withContext(ioContext){
            settings.getStringOrNull(host)
        }?.let { data ->
            Json.decodeFromString<CredentialDto>(data).toCredential()
        }

    override suspend fun putCredential(host: String, credential: Credential) {
        val data = Json.encodeToString(credential.toCredentialDto())
        withContext(ioContext){
            settings.putString(host, data)
        }
    }
}
