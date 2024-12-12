package com.jero3000.appinstaller.repository.credential.datasource

import com.jero3000.appinstaller.model.Credential
import com.jero3000.appinstaller.model.Settings
import com.jero3000.appinstaller.repository.preferences.ApplicationPreferences

class PersistenceSwitcher(private val diskDataSource : CredentialDataSource,
                          private val memoryDataSource: CredentialDataSource,
                          private val preferences: ApplicationPreferences) :  CredentialDataSource {

    private suspend fun getDataSource(): CredentialDataSource{
        return if(preferences.getBoolean(Settings.SAVE_CREDENTIALS.key) == true){
            diskDataSource
        } else {
            memoryDataSource
        }
    }

    override suspend fun getCredential(host: String): Credential? {
        return getDataSource().getCredential(host)
    }

    override suspend fun putCredential(host: String, credential: Credential) {
        getDataSource().putCredential(host, credential)
    }

    override suspend fun deleteCredential(host: String) {
        getDataSource().deleteCredential(host)
    }

    override suspend fun clear() {
        getDataSource().clear()
    }
}