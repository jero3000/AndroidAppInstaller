package com.jero3000.appinstaller.repository.config.json

import kotlinx.serialization.Serializable

@Serializable
data class AppConfigDto(
    val projects: List<ProjectDto>
)