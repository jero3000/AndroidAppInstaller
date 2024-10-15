package org.example.project.appinstaller.repository.config.json

import kotlinx.serialization.Serializable

@Serializable
data class BuildVariantDto(
    val name: String,
    val location: String,
    val packages: List<AppPackageDto>
)