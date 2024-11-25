package org.example.project.appinstaller.repository.file.datasource

import dev.zwander.kotlin.file.IPlatformFile
import kotlinx.io.IOException
import org.example.project.appinstaller.model.Credential
import org.example.project.appinstaller.platform.uri.UriParser

class FileDataSourceResolver(private val installedDataSources: List<FileDataSource>,
                             private val uriParser: UriParser) : FileDataSource {
    override fun supports(url: String): Boolean {
        return installedDataSources.any { it.supports(url) }
    }

    override suspend fun getFile(url: String, targetPath: String, credential: Credential?): Result<IPlatformFile> {
        val uriResult = kotlin.runCatching { //Ensure the url is well constructed
            uriParser.getProtocol(url)
        }
        return if(uriResult.isFailure){
            Result.failure(uriResult.exceptionOrNull() ?: Exception("Unknown error"))
        } else {
            installedDataSources.firstOrNull { it.supports(url) }?.getFile(url, targetPath, credential) ?: Result.failure(IOException("Invalid protocol!"))
        }
    }
}
