package com.jero3000.appinstaller.repository.config.json

import kotlinx.serialization.Serializable

@Serializable
data class PlaceholderDto(
    val id : String,
    val name: String,
    val value: String
)
