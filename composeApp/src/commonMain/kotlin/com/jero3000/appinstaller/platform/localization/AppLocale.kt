package com.jero3000.appinstaller.platform.localization

import androidappinstaller.composeapp.generated.resources.Res
import androidappinstaller.composeapp.generated.resources.en
import androidappinstaller.composeapp.generated.resources.es
import org.jetbrains.compose.resources.StringResource

enum class AppLocale(
    val code: String,
    val stringRes: StringResource
) {
    English("en", Res.string.en),
    Spanish("es", Res.string.es)
}
