package org.example.project.appinstaller.repository.credential.datasource

import com.russhwolf.settings.Settings
import dev.whyoleg.cryptography.CryptographyProvider
import dev.whyoleg.cryptography.algorithms.AES
import io.ktor.util.decodeBase64Bytes
import io.ktor.util.encodeBase64
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
            settings.getStringOrNull(host)?.let { decrypt(it) }
        }?.let { data ->
            Json.decodeFromString<CredentialDto>(data).toCredential()
        }

    override suspend fun putCredential(host: String, credential: Credential) {
        val data = Json.encodeToString(credential.toCredentialDto())
        withContext(ioContext){
            settings.putString(host, encrypt(data))
        }
    }

    private suspend fun encrypt(data: String): String{
        // getting default provider
        val provider = CryptographyProvider.Default
        // getting AES-GCM algorithm
        val aesGcm = provider.get(AES.GCM)

        //Encryption key
        val key: AES.GCM.Key = aesGcm.keyDecoder().decodeFromByteArray(AES.Key.Format.RAW, KEY)

        val cipher = key.cipher()
        val ciphertext: ByteArray = cipher.encrypt(plaintext = data.encodeToByteArray())

        return ciphertext.encodeBase64()
    }

    private suspend fun decrypt(data: String): String{
        // getting default provider
        val provider = CryptographyProvider.Default
        // getting AES-GCM algorithm
        val aesGcm = provider.get(AES.GCM)

        //Decryption key
        val key: AES.GCM.Key = aesGcm.keyDecoder().decodeFromByteArray(AES.Key.Format.RAW, KEY)

        val cipher = key.cipher()
        return cipher.decrypt(ciphertext = data.decodeBase64Bytes()).decodeToString()
    }

    companion object{
        @OptIn(ExperimentalUnsignedTypes::class)
        private val KEY = ubyteArrayOf(0xA9u,0xb6u,0xb0u,0x21u,0x2fu,0xf6u,0xf2u,0x6du,0xc6u,0xe7u,0x25u,
            0xb2u,0xd8u,0xbfu,0x3fu,0xf9u,0xacu,0xbau,0xecu,0x57u,0x9du,0xa1u,0xf9u,0xcdu,0xf8u,
            0xebu,0x93u,0x8du,0x93u,0xc2u,0xeau,0xdeu).toByteArray()
    }
}
