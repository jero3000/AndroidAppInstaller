package org.example.project.appinstaller.repository.file.datasource

import dev.zwander.kotlin.file.IPlatformFile
import kotlinx.io.IOException
import org.example.project.appinstaller.model.Credential

class FileDataSourceResolver(private val installedDataSources: List<FileDataSource>) : FileDataSource {
    override fun supports(url: String): Boolean {
        return installedDataSources.any { it.supports(url) }
    }

    override suspend fun getFile(url: String, targetPath: String, credential: Credential?): Result<IPlatformFile> {
        return installedDataSources.firstOrNull { it.supports(url) }?.getFile(url, targetPath, credential) ?: Result.failure(IOException("Invalid protocol!"))
    }
}
