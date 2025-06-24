package com.jero3000.appinstaller.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun SwitchRow(modifier: Modifier = Modifier,
              name: String,
              color: Color = MaterialTheme.colorScheme.surfaceVariant,
              checked: Boolean,
              onCheckedChanged: (Boolean) -> Unit
){
    Surface(modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        color = color) {
        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 15.dp).weight(1f),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                text = name
            )
            Switch(
                modifier = Modifier.padding(end = 15.dp),
                checked = checked,
                onCheckedChange = onCheckedChanged
            )
        }
    }
}
