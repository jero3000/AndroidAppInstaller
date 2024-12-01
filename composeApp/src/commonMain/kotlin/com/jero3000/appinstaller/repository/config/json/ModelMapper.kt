package com.jero3000.appinstaller.repository.config.json

import com.jero3000.appinstaller.model.AppConfig
import com.jero3000.appinstaller.model.AppPackage
import com.jero3000.appinstaller.model.BuildVariant
import com.jero3000.appinstaller.model.Project

fun com.jero3000.appinstaller.repository.config.json.AppPackageDto.toAppPackage() =  AppPackage(
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