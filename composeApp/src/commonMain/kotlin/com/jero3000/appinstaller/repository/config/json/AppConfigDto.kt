package com.jero3000.appinstaller.repository.config.json

import kotlinx.serialization.Serializable

@Serializable
data class AppConfigDto(
    val devices: List<DeviceDto> = emptyList(),
    val projects: List<ProjectDto>
)