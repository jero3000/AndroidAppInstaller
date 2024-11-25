package org.example.project.appinstaller.repository.config.json

import org.example.project.appinstaller.model.AppConfig
import org.example.project.appinstaller.model.AppPackage
import org.example.project.appinstaller.model.BuildVariant
import org.example.project.appinstaller.model.Project

fun AppPackageDto.toAppPackage() =  AppPackage(
    name,
    packageName,
    path
)

fun BuildVariantDto.toBuildVariant() = BuildVariant(
    name,
    location,
    deviceMap,
    packages.map { it.toAppPackage() }
)

fun ProjectDto.toProject() = Project(
    name,
    buildVariants.map { it.toBuildVariant() }
)

fun AppConfigDto.toAppConfig() = AppConfig(
    projects.map { it.toProject() }
)