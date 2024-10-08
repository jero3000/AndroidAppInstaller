package org.example.project.appinstaller.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import org.example.project.appinstaller.theme.AppColors
import org.example.project.appinstaller.theme.LocalAppColors

object CustomTheme {
    val colors: AppColors
        @Composable
        @ReadOnlyComposable
        get() = LocalAppColors.current
}