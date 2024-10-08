package org.example.project.appinstaller

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "AndroidAppInstaller",
    ) {
        App()
    }
}