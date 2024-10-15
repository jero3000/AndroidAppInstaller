package org.example.project.appinstaller.repository.config.json

import kotlinx.serialization.Serializable

@Serializable
data class AppConfigDto(
    val projects: List<ProjectDto>
)