package org.example.project.appinstaller.platform.filesystem

import net.harawata.appdirs.AppDirsFactory
import java.io.File
import java.nio.file.Paths


class PlatformFileSystemImpl : PlatformFileSystem {

    override fun getAppDataDirectory(appName: String, author: String): String {
        return AppDirsFactory.getInstance().getUserDataDir(appName, null, author)
    }

    override fun getUserDirectory(): String {
        return System.getProperty("user.home")
    }

    override fun getTempDirectory(): String {
        return System.getProperty("java.io.tmpdir")
    }

    override fun combine(path1: String, path2: String): String {
        return File(path1).resolve(path2).absolutePath
    }

    override fun getUri(path: String): String {
        return Paths.get(path).toUri().toString()
    }
}
