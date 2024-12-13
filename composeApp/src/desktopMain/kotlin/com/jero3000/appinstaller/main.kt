package com.jero3000.appinstaller

import androidappinstaller.composeapp.generated.resources.Res
import androidappinstaller.composeapp.generated.resources.about_version
import androidappinstaller.composeapp.generated.resources.exit_popup_title
import androidappinstaller.composeapp.generated.resources.installer_icon
import androidappinstaller.composeapp.generated.resources.menu_bar_file
import androidappinstaller.composeapp.generated.resources.menu_bar_file_exit
import androidappinstaller.composeapp.generated.resources.menu_bar_file_exit_mnemonic
import androidappinstaller.composeapp.generated.resources.menu_bar_file_load
import androidappinstaller.composeapp.generated.resources.menu_bar_file_load_mnemonic
import androidappinstaller.composeapp.generated.resources.menu_bar_file_mnemonic
import androidappinstaller.composeapp.generated.resources.menu_bar_file_settings
import androidappinstaller.composeapp.generated.resources.menu_bar_file_settings_mnemonic
import androidappinstaller.composeapp.generated.resources.menu_bar_help
import androidappinstaller.composeapp.generated.resources.menu_bar_help_about
import androidappinstaller.composeapp.generated.resources.menu_bar_help_languages
import androidappinstaller.composeapp.generated.resources.menu_bar_help_licenses
import androidappinstaller.composeapp.generated.resources.menu_bar_help_licenses_mnemonic
import androidappinstaller.composeapp.generated.resources.menu_bar_help_mnemonic
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.FrameWindowScope
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
import com.jero3000.appinstaller.di.appModule
import com.jero3000.appinstaller.model.Settings
import com.jero3000.appinstaller.platform.localization.AppLocale
import com.jero3000.appinstaller.platform.localization.LocaleManager
import com.jero3000.appinstaller.platform.localization.LocalizedApp
import com.jero3000.appinstaller.repository.preferences.ApplicationPreferences
import com.jero3000.appinstaller.ui.component.CustomAlertDialog
import com.jero3000.appinstaller.ui.screen.Application
import com.jero3000.appinstaller.ui.screen.MenuActions
import com.jero3000.appinstaller.ui.screen.settings.SettingsScreen
import com.jero3000.appinstaller.ui.screen.setup.SetupScreen
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.core.context.startKoin
import org.koin.java.KoinJavaComponent.inject

fun main() {
    startKoin {
        modules(appModule)
    }
    val localeManager: LocaleManager by inject(LocaleManager::class.java)
    val preferences: ApplicationPreferences by inject(ApplicationPreferences::class.java)
    val defaultLocale = runBlocking { preferences.getString(Settings.LANGUAGE.key)?.let {
        langCode -> AppLocale.entries.firstOrNull{ it.code == langCode }
    } } ?: localeManager.getLocale()
    localeManager.setLocale(defaultLocale)

    return application {
        var isOpen by remember { mutableStateOf(true) }
        var exitPopup by remember { mutableStateOf(false) }
        var isAboutOpen by remember { mutableStateOf(false) }
        var currentLocale by remember { mutableStateOf(defaultLocale) }
        Application.onExit = {
            isOpen = false
        }

        if (isOpen) {
            LocalizedApp(locale = currentLocale) {
                Window(
                    onCloseRequest = { exitPopup = true },
                    title = "Android application installer",
                    icon = painterResource(Res.drawable.installer_icon)
                ) {
                    NestedNavigation { navigator ->
                        val scope = rememberCoroutineScope()
                        CustomMenuBar(
                            currentLocale = currentLocale,
                            onLoad = {
                                MenuActions.loadConfiguration(scope)
                            },
                            onSettings = {
                                navigator push SettingsScreen()
                            },
                            onNewLocale = { locale ->
                                localeManager.setLocale(locale)
                                currentLocale = locale
                                scope.launch{ preferences.putString(Settings.LANGUAGE.key, locale.code) }
                            },
                            onAbout = {
                                isAboutOpen = true
                            },
                            onExit = {
                                exitPopup = true
                            }
                        )
                        MaterialTheme {
                            CurrentScreen()
                        }
                    }

                    if (exitPopup) {
                        CustomAlertDialog(stringResource(Res.string.exit_popup_title), null, "Ok", {
                            exitPopup = false
                        }) {
                            isOpen = false
                        }
                    }
                    if (isAboutOpen) {
                        AboutDialog {
                            isAboutOpen = false
                        }
                    }
                }
            }
        } else {
          exitApplication()
        }
    }
}


@OptIn(ExperimentalVoyagerApi::class)
@Composable
private fun NestedNavigation(
    content: NavigatorContent = { CurrentScreen() }
) {
    ProvideNavigatorLifecycleKMPSupport {
        Navigator(
            screen = SetupScreen()
        ) { navigator ->
            content(navigator)
        }
    }
}

@Composable
private fun FrameWindowScope.CustomMenuBar(currentLocale: AppLocale,
                                           onLoad: () -> Unit,
                                           onSettings: () -> Unit,
                                           onNewLocale: (locale: AppLocale) -> Unit,
                                           onAbout: () -> Unit,
                                           onExit: () -> Unit){
    MenuBar {
        Menu(stringResource(Res.string.menu_bar_file), mnemonic = stringResource(Res.string.menu_bar_file_mnemonic).first()) {
            Item(
                stringResource(Res.string.menu_bar_file_load),
                onClick = {
                    onLoad()
                },
                shortcut = KeyShortcut(Key.S, ctrl = true),
                mnemonic = stringResource(Res.string.menu_bar_file_load_mnemonic).first()
            )
            Separator()
            Item(
                stringResource(Res.string.menu_bar_file_settings),
                onClick = {
                    onSettings()
                },
                shortcut = KeyShortcut(Key.S, ctrl = true, alt = true),
                mnemonic = stringResource(Res.string.menu_bar_file_settings_mnemonic).first()
            )
            Separator()
            Item(
                stringResource(Res.string.menu_bar_file_exit),
                onClick = { onExit() },
                shortcut = KeyShortcut(Key.Escape),
                mnemonic = stringResource(Res.string.menu_bar_file_exit_mnemonic).first()
            )
        }
        Menu(stringResource(Res.string.menu_bar_help), mnemonic = stringResource(Res.string.menu_bar_help_mnemonic).first()) {
            val uriHandler = LocalUriHandler.current
            Item(
                stringResource(Res.string.menu_bar_help_licenses),
                onClick = {
                    uriHandler.openUri("https://raw.githubusercontent.com/jero3000/AndroidAppInstaller/refs/heads/master/LICENSE")
                },
                mnemonic = stringResource(Res.string.menu_bar_help_licenses_mnemonic).first()
            )
            Separator()
            Menu(stringResource(Res.string.menu_bar_help_languages)) {
                AppLocale.entries.forEach { locale ->
                    CheckboxItem(stringResource(locale.stringRes),
                        checked = locale.code == currentLocale.code,
                        onCheckedChange = {
                            onNewLocale(locale)
                        })
                }
            }
            if(!isMacOS()) {
                Separator()
                Item(
                    stringResource(Res.string.menu_bar_help_about),
                    onClick = { onAbout() },
                    mnemonic = stringResource(Res.string.menu_bar_help_about).first()
                )
            }
        }
    }
}

private fun isMacOS(): Boolean {
    val osName = System.getProperty("os.name").lowercase()
    return osName.contains("mac") || osName.contains("darwin")
}

@Composable
private fun AboutDialog(onCloseRequest: () -> Unit){
    DialogWindow(
        onCloseRequest = { onCloseRequest() },
        title = stringResource(Res.string.menu_bar_help_about),
        state = rememberDialogState(
            position = WindowPosition(Alignment.Center),
            size = DpSize(550.dp, 230.dp)
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row {
                Image(
                    modifier = Modifier.size(130.dp),
                    painter = painterResource(Res.drawable.installer_icon),
                    contentDescription = null
                )
                Column(modifier = Modifier.padding(start = 20.dp)) {
                    Text(
                        "Android application installer",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        modifier = Modifier.padding(top = 15.dp),
                        text = "© 2024 Jerónimo Muñoz. MIT license",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        modifier = Modifier.padding(top = 15.dp),
                        text = stringResource(Res.string.about_version) + System.getProperty(
                            "versionName",
                            "Unknown"
                        ),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
