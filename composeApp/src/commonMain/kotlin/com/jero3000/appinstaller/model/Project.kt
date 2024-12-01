package com.jero3000.appinstaller.model

data class Project(
    val name: String,
    val buildVariants: List<BuildVariant>,
)
