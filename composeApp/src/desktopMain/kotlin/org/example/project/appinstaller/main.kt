package org.example.project.appinstaller

import androidappinstaller.composeapp.generated.resources.Res
import androidappinstaller.composeapp.generated.resources.check_icon
import androidappinstaller.composeapp.generated.resources.installer_icon
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyShortcut
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberDialogState
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.jetpack.ProvideNavigatorLifecycleKMPSupport
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.NavigatorContent
import org.example.project.appinstaller.di.appModule
import org.example.project.appinstaller.platform.intent.BrowserLauncher
import org.example.project.appinstaller.ui.component.CustomAlertDialog
import org.example.project.appinstaller.ui.screen.MenuActions
import org.example.project.appinstaller.ui.screen.settings.SettingsScreen
import org.example.project.appinstaller.ui.screen.setup.SetupScreen
import org.jetbrains.compose.resources.painterResource
import org.koin.core.context.startKoin
import org.koin.java.KoinJavaComponent.inject

@OptIn(ExperimentalVoyagerApi::class)
fun main() = application {
    val icon = painterResource(Res.drawable.installer_icon)
    var isOpen by remember { mutableStateOf(true) }
    var exitPopup by remember { mutableStateOf(false) }
    var isAboutOpen by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    if (isOpen) {
        startKoin {
            modules(appModule)
        }

        Window(
            onCloseRequest = { exitPopup = true },
            title = "Android application installer",
            icon = icon
        ) {
            ProvideNavigatorLifecycleKMPSupport {
                NestedNavigation { navigator ->
                    MenuBar {
                        Menu("File", mnemonic = 'F') {
                            Item("Load configuration...",
                                onClick = {
                                    MenuActions.loadConfiguration(scope)
                                },
                                shortcut = KeyShortcut(Key.S, ctrl = true),
                                mnemonic = 'L'
                            )
                            Separator()
                            Item("Settings",
                                onClick = {
                                    navigator push SettingsScreen()
                                },
                                shortcut = KeyShortcut(Key.S, ctrl = true, alt = true),
                                mnemonic = 'S')
                            Separator()
                            Item(
                                "Exit",
                                onClick = { exitPopup = true },
                                shortcut = KeyShortcut(Key.Escape),
                                mnemonic = 'E'
                            )
                        }
                        Menu("Help", mnemonic = 'H'){
                            Item("Licenses",
                                onClick = {
                                    val browser : BrowserLauncher by inject(BrowserLauncher::class.java)
                                    browser.launchUrl("https://raw.githubusercontent.com/jero3000/AndroidAppInstaller/refs/heads/master/LICENSE")
                                },
                                mnemonic = 'L')
                            Separator()
                            Item(
                                "About",
                                onClick = { isAboutOpen = true },
                                mnemonic = 'A'
                            )
                        }
                    }
                    MaterialTheme {
                        CurrentScreen()
                    }
                }
            }
            if(exitPopup) {
                CustomAlertDialog("Are you sure?", null, "Ok", {
                    exitPopup = false
                }) {
                    isOpen = false
                }
            }
            if (isAboutOpen) {
                DialogWindow(
                    onCloseRequest = { isAboutOpen = false },
                    title = "About",
                    state = rememberDialogState(
                        position = WindowPosition(Alignment.Center),
                        size = DpSize(550.dp, 230.dp))
                ) {
                    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        Row{
                            Image(modifier = Modifier.size(130.dp),
                                painter = painterResource(Res.drawable.installer_icon),
                                contentDescription = null)
                            Column(modifier = Modifier.padding(start = 20.dp)) {
                                Text(
                                    "Android application installer",
                                    style = MaterialTheme.typography.headlineSmall
                                )
                                Text(modifier = Modifier.padding(top = 15.dp),
                                    text = "© 2024 Jerónimo Muñoz. MIT license",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(modifier = Modifier.padding(top = 15.dp),
                                    text = "Version: " + System.getProperty("versionName", "Unknown"),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
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