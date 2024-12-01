package org.example.project.appinstaller.repository.file

import dev.zwander.kotlin.file.IPlatformFile
import io.ktor.http.Url
import org.example.project.appinstaller.model.exception.CredentialsRequiredException
import org.example.project.appinstaller.platform.filesystem.FileUtils
import org.example.project.appinstaller.platform.filesystem.PlatformFileSystem
import org.example.project.appinstaller.platform.uri.UriParser
import org.example.project.appinstaller.repository.credential.CredentialRepository
import org.example.project.appinstaller.repository.file.datasource.FileDataSource

class FileRepositoryImpl(private val dataSource: FileDataSource,
                         private val platformFileSystem: PlatformFileSystem,
                         private val uriParser: UriParser,
                         private val credentialRepository: CredentialRepository,
                         private val fileUtils: FileUtils
): FileRepository {

    override fun getFile(url: String): IPlatformFile? {
        return kotlin.runCatching { uriParser.getFilename(url) }.getOrNull()?.let { fileName ->
            val tmpDir = platformFileSystem.combine(platformFileSystem.getTempDirectory(), TEMP_DIR_NAME)
            val filePath = platformFileSystem.combine(tmpDir, fileName)
            fileUtils.getFileFromPath(filePath, false)?.takeIf { it.getExists() }
        }
    }

    override suspend fun fetchFile(url: String): Result<IPlatformFile> {
        val tmpDir = platformFileSystem.combine(platformFileSystem.getTempDirectory(), TEMP_DIR_NAME)
        return fileUtils.getFileFromPath(tmpDir, true)?.let { dir ->
            val exists = if(!dir.getExists()){
                dir.mkdir()
            } else true

            if(exists){
                val credential = credentialRepository.getCredential(Url(url).host)
                dataSource.getFile(url, tmpDir, credential).also {
                    if(it.exceptionOrNull() is CredentialsRequiredException){
                        credentialRepository.deleteCredential(Url(url).host)
                    }
                }
            } else {
                Result.failure(Exception("Cannot create the temp dir"))
            }
        } ?: Result.failure(Exception("Cannot create the temp dir"))
    }

    override suspend fun clear() {
        val tmpDir = platformFileSystem.combine(platformFileSystem.getTempDirectory(), TEMP_DIR_NAME)
        fileUtils.getFileFromPath(tmpDir, true)?.let { dir ->
            dir.listFiles()?.forEach {
                it.delete()
            }
        }
    }

    companion object{
        private const val TEMP_DIR_NAME = "AndroidAppInstaller"
    }
}
