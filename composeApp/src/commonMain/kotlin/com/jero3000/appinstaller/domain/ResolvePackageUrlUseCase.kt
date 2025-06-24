package com.jero3000.appinstaller.domain

import com.jero3000.appinstaller.model.AppPackage
import com.jero3000.appinstaller.model.AppVersion
import com.jero3000.appinstaller.model.BuildVariant
import com.jero3000.appinstaller.model.Placeholder
import java.util.Locale

class ResolvePackageUrlUseCase {

    enum class Modifiers(val id: String){
        UPPER_CASE("uppercase"),
        LOWER_CASE("lowercase"),
        CAMEL_CASE("camelcase"),
    }

    operator fun invoke(buildVariant: BuildVariant,
                        appPackage: AppPackage,
                        appVersion: AppVersion,
                        manufacturer: String,
                        placeholders: List<Placeholder>): String{


        val placeholderMap = mutableMapOf(
            MAJOR_PLACEHOLDER to appVersion.major,
            MINOR_PLACEHOLDER to appVersion.minor,
            MICRO_PLACEHOLDER to appVersion.micro
        )
        placeholders.forEach{
            placeholderMap[it.id] = it.value
        }
        appVersion.build?.let { build ->
            placeholderMap.put(BUILD_PLACEHOLDER, build)
        }
        val deviceManufacturer = manufacturer.let {
            buildVariant.deviceMap[it] ?: it
        }
        placeholderMap[DEVICE_PLACEHOLDER] = deviceManufacturer

        val path = appPackage.altPath[deviceManufacturer] ?: appPackage.path
        val unresolvedUrl =
            buildVariant.location.takeIf { it.endsWith('/') }?.let { it + path.removePrefix("/") }
                ?: (buildVariant.location + "/" + path.removePrefix("/"))
        var resolvedUrl = unresolvedUrl

        val regex = Regex("""\{(\w+)(:(\w+))?\}""")
        regex.findAll(unresolvedUrl).forEach { match ->
            val placeholder = match.groupValues[1]
            val modifier = match.groupValues.getOrNull(3)  // Optional
            placeholderMap[placeholder]?.let { value ->
                if (!modifier.isNullOrEmpty()) {
                    val valueModified = when (modifier) {
                        Modifiers.UPPER_CASE.id -> value.uppercase()
                        Modifiers.LOWER_CASE.id -> value.lowercase()
                        Modifiers.CAMEL_CASE.id -> value.replaceFirstChar { it.uppercase() }
                        else -> value
                    }
                    resolvedUrl = resolvedUrl.replace(match.value, valueModified)
                } else {
                    resolvedUrl = resolvedUrl.replace(match.value, value)
                }
            } ?: run {
                resolvedUrl = resolvedUrl.replace(match.value, "")
            }
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


