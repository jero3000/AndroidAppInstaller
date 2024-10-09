package org.example.project.appinstaller.repository.json

import kotlinx.serialization.Serializable

@Serializable
data class AppPackageDto(
    val name : String,
    val packageName: String,
    val path: String,
)