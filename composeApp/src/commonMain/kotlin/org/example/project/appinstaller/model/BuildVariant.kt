package org.example.project.appinstaller.model

data class BuildVariant(
    val name: String,
    val location: String,
    val deviceMap: Map<String, String>,
    val packages: List<AppPackage>
)
