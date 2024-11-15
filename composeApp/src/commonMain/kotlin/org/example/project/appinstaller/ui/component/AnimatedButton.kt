package org.example.project.appinstaller.ui.component

import androidappinstaller.composeapp.generated.resources.Res
import androidappinstaller.composeapp.generated.resources.check_icon
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import kotlin.time.Duration.Companion.seconds

@Composable
fun AnimatedButton(modifier: Modifier = Modifier, text: String, onClick: () -> Unit){
    var showIcon by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Button(
            onClick = {
                showIcon = true
                coroutineScope.launch {
                    delay(2.seconds)
                    showIcon = false
                }
                onClick()
            }) {
            Text(text)
        }
        AnimatedVisibility(showIcon) {
            Row(modifier = Modifier.size(20.dp)) {
                Image(painterResource(Res.drawable.check_icon), null)
            }
        }
    }
}