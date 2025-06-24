package com.jero3000.appinstaller.model

data class  AppConfig(
    val placeholders: List<Placeholder>,
    val devices: List<AndroidDevice>,
    val projects: List<Project>
)
