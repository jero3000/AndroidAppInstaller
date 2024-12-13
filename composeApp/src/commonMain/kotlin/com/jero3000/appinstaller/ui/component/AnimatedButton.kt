package com.jero3000.appinstaller.ui.component

import androidappinstaller.composeapp.generated.resources.Res
import androidappinstaller.composeapp.generated.resources.check_icon
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.TooltipPlacement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AnimatedButton(modifier: Modifier = Modifier, enabled: Boolean, text: String, tooltip: String, onClick: () -> Unit){
    var showIcon by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        TooltipArea(
            tooltip = {
                // Composable tooltip content:
                Surface(
                    modifier = Modifier.shadow(4.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = tooltip,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            },
            delayMillis = 600, // In milliseconds
            tooltipPlacement = TooltipPlacement.CursorPoint(
                alignment = Alignment.BottomEnd,
                offset =  DpOffset((-16).dp, 0.dp) // Tooltip offset
            )
        ) {
            Button(
                enabled = enabled,
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
        }
        AnimatedVisibility(showIcon) {
            Row(modifier = Modifier.size(20.dp)) {
                Image(painterResource(Res.drawable.check_icon), null)
            }
        }
    }
}