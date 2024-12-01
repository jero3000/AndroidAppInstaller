package com.jero3000.appinstaller.repository.config.json

import kotlinx.serialization.Serializable

@Serializable
data class AppPackageDto(
    val name : String,
    val packageName: String,
    val path: String,
)