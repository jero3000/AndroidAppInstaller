package com.jero3000.appinstaller.theme

import androidx.compose.runtime.Stable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Stable
data class AppColors(
    val error: Color,
    val warning: Color,
    val success: Color,
    val idle: Color,
    val darkGreen: Color,
    val darkRed: Color
)

val DefaultColors = AppColors(
    error = Color(0xFFFB8587),
    warning = Color(0xFFFBB085),
    success = Color(0xFFB0FB85),
    idle = Color(0xFF8F8E8D),
    darkGreen = Color(0xFF0D9900),
    darkRed = Color(0xFFB30000)
)

val LocalAppColors = staticCompositionLocalOf {
    DefaultColors
}
