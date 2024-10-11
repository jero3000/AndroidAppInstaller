package org.example.project.appinstaller.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

@Composable
fun AppRow(modifier: Modifier = Modifier,
           appName: String,
           color: Color,
           checked: Boolean,
           state: String,
           isTransient: Boolean,
           onCheckedChanged: (Boolean) -> Unit){
    var selected by remember { mutableStateOf(checked) }
    Row (modifier = modifier,
        verticalAlignment = Alignment.CenterVertically){
        Column {
            Row(modifier = Modifier
                .width(250.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(color),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Text(modifier = Modifier.padding(start = 15.dp ), text = appName)
                Switch(
                    modifier = Modifier.semantics { contentDescription = "App to install" }
                        .padding(end = 15.dp),
                    checked = selected,
                    onCheckedChange = { selected = it ; onCheckedChanged(it) }
                )
            }
        }
        Column {
            Row(verticalAlignment = Alignment.CenterVertically){
                val alpha = if(isTransient) 1f else 0f
                Column (modifier = Modifier.padding(start = 10.dp)) {
                    Text(text = state)
                }
                Column (modifier = Modifier.padding(start = 10.dp)
                    .alpha(alpha).size(20.dp)){
                    CircularProgressIndicator()
                }
            }
        }
    }
}