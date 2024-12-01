package com.jero3000.appinstaller.model

import dev.zwander.kotlin.file.IPlatformFile

data class AppPackage(
    val name : String,
    val packageName: String,
    val path: String,
    var packageFile: IPlatformFile? = null
)
