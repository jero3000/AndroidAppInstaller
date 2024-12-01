package com.jero3000.appinstaller.platform.filesystem

interface PlatformFileSystem {
    fun getAppDataDirectory(appName: String, author: String): String
    fun getUserDirectory(): String
    fun getTempDirectory(): String
    //fun getFileSeparator(): String
    fun combine(path1: String, path2: String): String
    fun getUri(path: String): String
}
