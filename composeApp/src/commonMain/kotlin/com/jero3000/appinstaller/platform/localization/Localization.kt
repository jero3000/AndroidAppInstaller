package com.jero3000.appinstaller.platform.localization

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

val LocalLocalization = staticCompositionLocalOf { AppLocale.English }

@Composable
fun LocalizedApp(locale: AppLocale = AppLocale.English, content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalLocalization provides locale,
        content = content
    )
}
