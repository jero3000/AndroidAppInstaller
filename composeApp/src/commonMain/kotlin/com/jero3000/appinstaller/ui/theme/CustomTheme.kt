package com.jero3000.appinstaller.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import com.jero3000.appinstaller.theme.AppColors
import com.jero3000.appinstaller.theme.LocalAppColors

object CustomTheme {
    val colors: AppColors
        @Composable
        @ReadOnlyComposable
        get() = LocalAppColors.current
}