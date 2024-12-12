package com.jero3000.appinstaller.domain

import com.jero3000.appinstaller.model.Settings
import com.jero3000.appinstaller.repository.credential.CredentialRepository
import dev.zwander.kotlin.file.IPlatformFile
import com.jero3000.appinstaller.repository.file.FileRepository
import com.jero3000.appinstaller.repository.preferences.ApplicationPreferences

class GetPackageFileUseCase(private val fileRepository: FileRepository,
                            private val credentialRepository: CredentialRepository,
                            private val preferences: ApplicationPreferences) {

    suspend operator fun invoke(url: String): Result<IPlatformFile>{
        val result = fileRepository.getFile(url)?.let { Result.success(it) } ?: run{
            fileRepository.fetchFile(url).also {
                if(preferences.getBoolean(Settings.SAVE_CREDENTIALS.key) != true){
                    credentialRepository.clear()
                }
            }
        }

        return result
    }
}