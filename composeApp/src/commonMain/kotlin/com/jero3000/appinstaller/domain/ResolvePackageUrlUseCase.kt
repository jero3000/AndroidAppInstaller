package com.jero3000.appinstaller.domain

import com.jero3000.appinstaller.model.AppPackage
import com.jero3000.appinstaller.model.AppVersion
import com.jero3000.appinstaller.model.BuildVariant

class ResolvePackageUrlUseCase {

    operator fun invoke(buildVariant: BuildVariant,
                        appPackage: AppPackage,
                        appVersion: AppVersion,
                        manufacturer: String): String{


        val placeholders = mutableMapOf(
            MAJOR_PLACEHOLDER to appVersion.major,
            MINOR_PLACEHOLDER to appVersion.minor,
            MICRO_PLACEHOLDER to appVersion.micro
        )
        appVersion.build?.let { build ->
            placeholders.put(BUILD_PLACEHOLDER, build)
        }
        val deviceManufacturer = manufacturer.let {
            buildVariant.deviceMap[it] ?: it
        }
        placeholders[DEVICE_PLACEHOLDER] = deviceManufacturer

        val unresolvedUrl =
            buildVariant.location.takeIf { it.endsWith('/') }?.let { it + appPackage.path.removePrefix("/") }
                ?: (buildVariant.location + "/" + appPackage.path.removePrefix("/"))
        var resolvedUrl = unresolvedUrl
        placeholders.forEach { placeholder ->
            resolvedUrl = resolvedUrl.replace("{${placeholder.key}}", placeholder.value)
        }

        return resolvedUrl
    }

    companion object{
        private const val MAJOR_PLACEHOLDER = "major"
        private const val MINOR_PLACEHOLDER = "minor"
        private const val MICRO_PLACEHOLDER = "micro"
        private const val BUILD_PLACEHOLDER = "build"
        private const val DEVICE_PLACEHOLDER = "device"
    }
}
