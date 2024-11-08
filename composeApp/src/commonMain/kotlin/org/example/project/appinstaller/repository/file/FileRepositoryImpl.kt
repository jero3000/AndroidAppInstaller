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
        val fileName = uriParser.getFilename(url)
        val tmpDir = platformFileSystem.getTempDirectory()
        val fileSeparator = platformFileSystem.getFileSeparator()
        val filePath = tmpDir.takeIf { it.endsWith(fileSeparator) }
            ?.let { it + TEMP_DIR_NAME + fileSeparator + fileName } ?: (tmpDir + fileSeparator + TEMP_DIR_NAME + fileSeparator + fileName)
        return fileUtils.getFileFromPath(platformFileSystem.getUri(filePath), false)?.takeIf { it.getExists() }
    }

    override suspend fun fetchFile(url: String): Result<IPlatformFile> {
        val tmpDir = platformFileSystem.getTempDirectory()
        val dirPath = tmpDir.takeIf { it.endsWith(platformFileSystem.getFileSeparator()) }
            ?.let { it + TEMP_DIR_NAME } ?: (tmpDir + platformFileSystem.getFileSeparator() + TEMP_DIR_NAME)
        return fileUtils.getFileFromPath(platformFileSystem.getUri(dirPath), true)?.let { dir ->
            val exists = if(!dir.getExists()){
                dir.mkdir()
            } else true

            if(exists){
                val credential = credentialRepository.getCredential(Url(url).host)
                dataSource.getFile(url, dirPath, credential).also {
                    if(it.exceptionOrNull() is CredentialsRequiredException){
                        credentialRepository.deleteCredential(Url(url).host)
                    }
                }
            } else {
                Result.failure(Exception("Cannot create the temp dir"))
            }
        } ?: Result.failure(Exception("Cannot create the temp dir"))
    }

    companion object{
        private const val TEMP_DIR_NAME = "AndroidAppInstaller"
    }
}
