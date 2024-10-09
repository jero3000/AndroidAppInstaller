package org.example.project.appinstaller.repository.json

import kotlinx.serialization.Serializable

@Serializable
data class ProjectDto(
    val name: String,
    val buildVariants: List<BuildVariantDto>,
)