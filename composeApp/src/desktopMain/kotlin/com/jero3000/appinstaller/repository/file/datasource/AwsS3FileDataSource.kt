package com.jero3000.appinstaller.repository.file.datasource

import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.GetObjectRequest
import aws.smithy.kotlin.runtime.content.writeToOutputStream
import com.jero3000.appinstaller.model.Credential
import dev.zwander.kotlin.file.FileUtils
import dev.zwander.kotlin.file.IPlatformFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.io.IOException
import kotlinx.io.files.Path
import java.io.File
import java.io.FileOutputStream
import java.net.URI
import java.nio.file.Paths

class AwsS3FileDataSource : FileDataSource {
    override fun supports(url: String) = URI(url).scheme == "s3"

    override suspend fun getFile(
        url: String,
        targetPath: String,
        credential: Credential?
    ): Result<IPlatformFile> = withContext(Dispatchers.IO) {
        runCatching {
            val uri = URI(url)
            val regionStr = uri.authority
            val paths = Paths.get(uri.path)
            val bucketStr = paths.first().toString()
            val keyList = paths.toList().takeLast(paths.count() - 1).map { it.toString() }
            val keyStr = Paths.get("", *keyList.toTypedArray()).toString()
            val filename = Path(uri.path).name
            val targetFilePath =
                targetPath.takeIf { it.endsWith(File.separator) }?.let { it + filename }
                    ?: (targetPath + File.separator + filename)
            println("Downloading from region: $regionStr bucket: $bucketStr key: $keyStr")

            val request = GetObjectRequest {
                bucket = bucketStr
                key = keyStr
            }
            S3Client {
                region = regionStr
            }.use { s3 ->
                s3.getObject(request) { response ->
                    response.body?.let { stream ->
                        FileOutputStream(targetFilePath).use {
                            stream.writeToOutputStream(it)
                        }
                    } ?: throw IOException("Unable to download the file")
                }
            }
            val fileUri = Paths.get(targetFilePath).toUri().toString()
            FileUtils.fromString(fileUri, false) ?: throw IOException("Unable to create the target file")
        }
    }
}
