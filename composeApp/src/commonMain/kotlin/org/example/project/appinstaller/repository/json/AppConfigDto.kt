package org.example.project.appinstaller.repository.json

import kotlinx.serialization.Serializable

@Serializable
data class AppConfigDto(
    val projects: List<ProjectDto>
)