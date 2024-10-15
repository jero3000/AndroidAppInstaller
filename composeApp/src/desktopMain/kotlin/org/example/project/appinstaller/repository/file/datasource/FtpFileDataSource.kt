package org.example.project.appinstaller.repository.file.datasource

import dev.zwander.kotlin.file.FileUtils
import dev.zwander.kotlin.file.IPlatformFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.io.IOException
import kotlinx.io.files.Path
import org.apache.commons.net.ftp.FTP
import org.apache.commons.net.ftp.FTPClient
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URI
import java.nio.file.Paths


actual class FtpFileDataSource(): FileDataSource {
    override fun supports(url: String) = URI(url).scheme == "ftp"


    override suspend fun getFile(url: String, targetPath: String): Result<IPlatformFile> = withContext(Dispatchers.IO){
        kotlin.runCatching {
            var ftp: FTPClient? = null
            val uri = URI(url)
            val server = uri.host
            val port = uri.port.takeIf { it != -1 } ?: FTP.DEFAULT_PORT
            val path = uri.path
            val filename = Path(path).name
            val targetFilePath =
                targetPath.takeIf { it.endsWith(File.separator) }?.let { it + filename }
                    ?: (targetPath + File.separator + filename)
            try {
                ftp = FTPClient()
                ftp.connect(server, port)

                ftp.setFileType(FTP.BINARY_FILE_TYPE)
                ftp.enterLocalPassiveMode()

                val success =
                    BufferedOutputStream(FileOutputStream(targetFilePath)).use { outputStream ->
                        ftp.retrieveFile(path, outputStream)
                    }

                if (success) {
                    val fileUri = Paths.get(targetFilePath).toUri().toString()
                    FileUtils.fromString(fileUri, false) ?: throw IOException("Unable to create the target file")
                } else throw IOException("Unable to download the file from FTP server")
            } finally {
                if (ftp != null) {
                    ftp.logout()
                    ftp.disconnect()
                }
            }
        }
    }
}
