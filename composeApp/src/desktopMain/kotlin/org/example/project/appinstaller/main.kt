package org.example.project.appinstaller

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.key.KeyShortcut
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.input.key.Key
import org.example.project.appinstaller.di.appModule
import org.koin.core.context.startKoin

fun main() = application {

    var isOpen by remember { mutableStateOf(true) }
    if (isOpen) {
        startKoin {
            modules(appModule)
        }

        Window(
            onCloseRequest = ::exitApplication,
            title = "Android application installer",
        ) {
            MenuBar {
                Menu("File", mnemonic = 'F') {
                    Item("Load configuration...", onClick = { })
                    Separator()
                    Item("Settings", onClick = { })
                    Separator()
                    Item(
                        "Exit",
                        onClick = { isOpen = false },
                        shortcut = KeyShortcut(Key.Escape),
                        mnemonic = 'E'
                    )
                }
            }
            App()
        }
    }
}