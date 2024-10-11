package org.example.project.appinstaller.platform.filesystem

import java.io.File
import java.nio.file.Paths


actual class PlatformFileSystemImpl : PlatformFileSystem {

    override fun getUserDirectory(): String {
        return System.getProperty("user.home")
    }

    override fun getTempDirectory(): String {
        return System.getProperty("java.io.tmpdir")
    }

    override fun getFileSeparator(): String {
        return File.separator
    }

    override fun getUri(path: String): String {
        return Paths.get(path).toUri().toString()
    }
}
