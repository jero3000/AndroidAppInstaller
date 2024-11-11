package org.example.project.appinstaller

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyShortcut
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.jetpack.ProvideNavigatorLifecycleKMPSupport
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.NavigatorContent
import org.example.project.appinstaller.di.appModule
import org.example.project.appinstaller.ui.screen.MenuActions
import org.example.project.appinstaller.ui.screen.settings.SettingsScreen
import org.example.project.appinstaller.ui.screen.setup.SetupScreen
import org.koin.core.context.startKoin

@OptIn(ExperimentalVoyagerApi::class)
fun main() = application {
    var isOpen by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    if (isOpen) {
        startKoin {
            modules(appModule)
        }

        Window(
            onCloseRequest = ::exitApplication,
            title = "Android application installer",
        ) {
            ProvideNavigatorLifecycleKMPSupport {
                NestedNavigation { navigator ->
                    MenuBar {
                        Menu("File", mnemonic = 'F') {
                            Item("Load configuration...", onClick = {
                                MenuActions.loadConfiguration(scope)
                            })
                            Separator()
                            Item("Settings", onClick = {
                                navigator push SettingsScreen()
                            })
                            Separator()
                            Item(
                                "Exit",
                                onClick = { isOpen = false },
                                shortcut = KeyShortcut(Key.Escape),
                                mnemonic = 'E'
                            )
                        }
                    }
                    MaterialTheme {
                        CurrentScreen()
                    }
                }
            }
        }
    }
}

@Composable
private fun NestedNavigation(
    content: NavigatorContent = { CurrentScreen() }
) {
    Navigator(
        screen = SetupScreen()
    ) { navigator ->
        content(navigator)
    }
}