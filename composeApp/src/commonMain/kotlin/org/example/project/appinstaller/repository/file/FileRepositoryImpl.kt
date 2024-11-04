package org.example.project.appinstaller.repository.file

import dev.zwander.kotlin.file.FileUtils
import dev.zwander.kotlin.file.IPlatformFile
import io.ktor.http.Url
import org.example.project.appinstaller.platform.filesystem.PlatformFileSystem
import org.example.project.appinstaller.platform.uri.UriParser
import org.example.project.appinstaller.repository.credential.CredentialRepository
import org.example.project.appinstaller.repository.file.datasource.FileDataSource

class FileRepositoryImpl(private val dataSource: FileDataSource,
                         private val platformFileSystem: PlatformFileSystem,
                         private val uriParser: UriParser,
                         private val credentialRepository: CredentialRepository
): FileRepository {

    override fun getFile(url: String): IPlatformFile? {
        val fileName = uriParser.getFilename(url)
        val tmpDir = platformFileSystem.getTempDirectory()
        val filePath = tmpDir.takeIf { it.endsWith(platformFileSystem.getFileSeparator()) }
            ?.let { it + fileName } ?: (tmpDir + platformFileSystem.getFileSeparator() + fileName)
        return FileUtils.fromString(platformFileSystem.getUri(filePath), false)?.takeIf { it.getExists() }
    }

    override suspend fun fetchFile(url: String): Result<IPlatformFile> {
        val credential = credentialRepository.getCredential(Url(url).host)
        return dataSource.getFile(url, platformFileSystem.getTempDirectory(), credential)
    }
}
