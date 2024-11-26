package org.example.project.appinstaller.model

data class AppVersion(
    val major: String,
    val minor: String,
    val micro: String,
    val build: String? = null
)
