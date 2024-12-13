package com.jero3000.appinstaller.repository.config.json

import kotlinx.serialization.Serializable

@Serializable
data class BuildVariantDto(
    val name: String,
    val location: String,
    val deviceMap: Map<String, String> = emptyMap(),
    val packages: List<AppPackageDto>
)