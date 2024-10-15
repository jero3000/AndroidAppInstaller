package org.example.project.appinstaller.repository.file

import dev.zwander.kotlin.file.FileUtils
import dev.zwander.kotlin.file.IPlatformFile
import org.example.project.appinstaller.platform.filesystem.PlatformFileSystem
import org.example.project.appinstaller.platform.uri.UriParser
import org.example.project.appinstaller.repository.file.datasource.FileDataSource

class FileRepositoryImpl(private val dataSource: FileDataSource,
                         private val platformFileSystem: PlatformFileSystem,
                         private val uriParser: UriParser): FileRepository {

    override fun getFile(url: String): IPlatformFile? {
        val fileName = uriParser.getFilename(url)
        val tmpDir = platformFileSystem.getTempDirectory()
        val filePath = tmpDir.takeIf { it.endsWith(platformFileSystem.getFileSeparator()) }
            ?.let { it + fileName } ?: (tmpDir + platformFileSystem.getFileSeparator() + fileName)
        return FileUtils.fromString(platformFileSystem.getUri(filePath), false)?.takeIf { it.getExists() }
    }

    override suspend fun fetchFile(url: String): Result<IPlatformFile> {
        return dataSource.getFile(url, platformFileSystem.getTempDirectory())
    }
}