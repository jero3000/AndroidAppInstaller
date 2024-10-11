package org.example.project.appinstaller

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.example.project.appinstaller.di.appModule
import org.koin.core.context.startKoin

fun main() = application {
    startKoin {
        modules(appModule)
    }

    Window(
        onCloseRequest = ::exitApplication,
        title = "AndroidAppInstaller",
    ) {
        App()
    }
}