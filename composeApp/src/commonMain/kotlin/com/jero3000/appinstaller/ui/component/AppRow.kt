package com.jero3000.appinstaller.ui.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AppRow(modifier: Modifier = Modifier,
           appName: String,
           color: Color,
           checked: Boolean,
           state: String,
           isTransient: Boolean,
           onCheckedChanged: (Boolean) -> Unit){
    val alpha = if(isTransient) 1f else 0f
    Row(modifier = modifier,
        verticalAlignment = Alignment.CenterVertically) {
        SwitchRow(
            modifier = Modifier.width(250.dp),
            name = appName,
            color = color,
            checked = checked,
            onCheckedChanged = onCheckedChanged
        )
        Text(modifier = Modifier.padding(start = 10.dp), text = state)
        CircularProgressIndicator(
            modifier = Modifier
                .padding(start = 10.dp)
                .alpha(alpha)
                .size(20.dp)
        )
    }
}
