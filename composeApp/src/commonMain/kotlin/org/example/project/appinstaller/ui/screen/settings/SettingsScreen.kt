package org.example.project.appinstaller.ui.screen.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.example.project.appinstaller.platform.device.Device
import org.example.project.appinstaller.ui.component.AnimatedButton
import org.example.project.appinstaller.ui.component.RadioEntry
import org.example.project.appinstaller.ui.component.RadioGroup
import org.example.project.appinstaller.ui.screen.settings.model.SettingsEvent

class SettingsScreen : Screen{

    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<SettingsScreenModel>()
        val navigator = LocalNavigator.currentOrThrow
        Column {
            TextButton( modifier =  Modifier.padding(start = 5.dp), onClick = {
                navigator.popUntilRoot()
            }) {
                Text("< back")
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                RadioGroup(modifier = Modifier.width(250.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(Color.LightGray),
                    title = "Install mode",
                    radioOptions = provideInstallationModes()
                ){
                    screenModel.onEvent(SettingsEvent.OnInstallModeChanged(it.key))
                }
                Row(modifier = Modifier.padding(top = 20.dp)){
                    AnimatedButton(text = "Clear credentials") {
                        screenModel.onEvent(SettingsEvent.OnClearCredentials)
                    }
                    AnimatedButton(modifier = Modifier.padding(start = 10.dp), text = "Clear cache") {
                        screenModel.onEvent(SettingsEvent.OnClearCache)
                    }
                }
            }
        }
    }
}

fun provideInstallationModes() =
    Device.InstallMode.entries.map {
        when (it) {
            Device.InstallMode.NORMAL -> RadioEntry(
                "Normal",
                "Normal installation mode",
                Device.InstallMode.NORMAL.key
            )
            Device.InstallMode.DOWNGRADE -> RadioEntry(
                "Downgrade",
                "Allows version downgrade keeping the application data",
                Device.InstallMode.DOWNGRADE.key
            )
            Device.InstallMode.CLEAN -> RadioEntry(
                "Clean",
                "Uninstall the app and data if present, then performs a clean install",
                Device.InstallMode.CLEAN.key
            )
        }
    }
