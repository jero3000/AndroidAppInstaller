package org.example.project.appinstaller.theme

import androidx.compose.runtime.Stable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Stable
data class AppColors(
    val error: Color,
    val warning: Color,
    val success: Color
)

val DefaultColors = AppColors(
    error = Color(0xFFFB8587),
    warning = Color(0xFFFBB085),
    success = Color(0xFFB0FB85)
)

val LocalAppColors = staticCompositionLocalOf {
    DefaultColors
}
