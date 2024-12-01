package com.jero3000.appinstaller.repository.config.json

import kotlinx.serialization.Serializable

@Serializable
data class ProjectDto(
    val name: String,
    val buildVariants: List<BuildVariantDto>,
)