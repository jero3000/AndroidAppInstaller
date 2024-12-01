package com.jero3000.appinstaller.model

data class AppVersion(
    val major: String,
    val minor: String,
    val micro: String,
    val build: String? = null
)
