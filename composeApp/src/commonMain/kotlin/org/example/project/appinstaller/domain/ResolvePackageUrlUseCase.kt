package org.example.project.appinstaller.domain

import org.example.project.appinstaller.model.AppPackage
import org.example.project.appinstaller.model.BuildVariant

class ResolvePackageUrlUseCase {

    operator fun invoke(buildVariant: BuildVariant,
                        appPackage: AppPackage,
                        placeholders: Map<String, String>): String{
        val unresolvedUrl =
            buildVariant.location.takeIf { it.endsWith('/') }?.let { it + appPackage.path }
                ?: (buildVariant.location + "/" + appPackage.path)
        var resolvedUrl = unresolvedUrl
        placeholders.forEach { placeholder ->
            resolvedUrl = resolvedUrl.replace("{${placeholder.key}}", placeholder.value)
        }

        return resolvedUrl
    }

    companion object{
        const val MAJOR_PLACEHOLDER = "major"
        const val MINOR_PLACEHOLDER = "minor"
        const val MICRO_PLACEHOLDER = "micro"
        const val BUILD_PLACEHOLDER = "build"
        const val DEVICE_PLACEHOLDER = "device"
    }
}
