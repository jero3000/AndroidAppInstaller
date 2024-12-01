package com.jero3000.appinstaller.ui.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import com.jero3000.appinstaller.ui.theme.CustomTheme

@Composable
fun StatusSticker(modifier: Modifier = Modifier, okStatusText: String, failStatusText: String, okStatus: Boolean) {
    val color = if(okStatus) CustomTheme.colors.darkGreen else CustomTheme.colors.darkRed
    val alpha = remember { Animatable(0.7f) }
    if(!okStatus) {
        LaunchedEffect(Unit) {
            while (isActive) {
                delay(500) // Pulse the alpha every pulseRateMs to alert the user
                alpha.animateTo(0f, animationSpec = tween(durationMillis = 1000))
                alpha.animateTo(0.7f, animationSpec = tween(durationMillis = 1000))
            }
        }
    }
    OutlinedCard(modifier = modifier.alpha(alpha.value),
        colors = CardDefaults.outlinedCardColors().copy(contentColor = color, containerColor = Color.White),
        border = BorderStroke(3.dp, color)
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if(okStatus) {
                Text(modifier = Modifier.padding(10.dp),
                    text = okStatusText, textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 1.em)
            } else {
                Text(modifier = Modifier.padding(10.dp),
                    text = failStatusText,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 1.em)
            }
        }
    }
}
