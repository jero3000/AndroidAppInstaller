package com.jero3000.appinstaller.repository.config.json

import com.jero3000.appinstaller.model.AndroidDevice
import com.jero3000.appinstaller.model.AppConfig
import com.jero3000.appinstaller.model.AppPackage
import com.jero3000.appinstaller.model.BuildVariant
import com.jero3000.appinstaller.model.Placeholder
import com.jero3000.appinstaller.model.Project

fun AppPackageDto.toAppPackage() =  AppPackage(
    name,
    packageName,
    path,
    altPath
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

fun DeviceDto.toAndroidDevice() = AndroidDevice(
    "",
    manufacturer,
    manufacturer,
    true
)

fun PlaceholderDto.toPlaceholder() = Placeholder(
    id,
    name,
    value
)

fun AppConfigDto.toAppConfig() = AppConfig(
    placeholders.map { it.toPlaceholder() },
    devices.map { it.toAndroidDevice() },
    projects.map { it.toProject() }
)
