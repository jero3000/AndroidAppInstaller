package com.jero3000.appinstaller.platform.filesystem

import dev.zwander.kotlin.file.IPlatformFile

interface FileSystem {

    fun readTextFile(file: IPlatformFile): Result<String>

    fun writeTextFile(data: String, file: IPlatformFile): Result<Unit>
}
