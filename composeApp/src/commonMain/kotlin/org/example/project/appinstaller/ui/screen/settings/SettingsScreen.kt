package org.example.project.appinstaller.ui.screen.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.example.project.appinstaller.ui.component.AnimatedButton
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
                Row{
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
