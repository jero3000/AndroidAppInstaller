package org.example.project.appinstaller.filesystem

import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

//TODO to review https://github.com/vinceglb/FileKit?tab=readme-ov-file
actual class FileSystemImpl : FileSystem {
    override fun readConfiguration(fileName: String): Result<String> {
        val userHome = System.getProperty("user.home")
        val configurationFilePath = userHome + File.separator + ".AndroidAppInstaller" + File.separator + fileName

        return runCatching {
            Files.readString(Paths.get(configurationFilePath))
        }
    }
}
