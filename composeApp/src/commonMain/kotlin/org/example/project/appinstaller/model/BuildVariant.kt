package org.example.project.appinstaller.model

data class BuildVariant(
    val name: String,
    val location: String,
    val packages: List<AppPackage>
)
