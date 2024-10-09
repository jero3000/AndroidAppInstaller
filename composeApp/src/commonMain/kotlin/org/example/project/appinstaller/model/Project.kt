package org.example.project.appinstaller.model

data class Project(
    val name: String,
    val buildVariants: List<BuildVariant>,
)
