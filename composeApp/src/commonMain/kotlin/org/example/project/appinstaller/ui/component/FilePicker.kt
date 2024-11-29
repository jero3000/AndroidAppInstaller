package org.example.project.appinstaller.ui.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FilePicker(modifier: Modifier = Modifier, label: String, filePath : String, buttonText: String, onClick : () -> Unit){
    Row(modifier = modifier.wrapContentWidth().padding(start = 20.dp, end = 20.dp),
        verticalAlignment = Alignment.CenterVertically) {
        TextField(
            modifier = Modifier.width(500.dp),
            enabled = false,
            value = filePath,
            onValueChange = { },
            label = { Text(label, style = MaterialTheme.typography.labelSmall) },
            singleLine = true
        )
        Button( modifier = Modifier.padding(start = 10.dp),
            onClick = {
                onClick()
            }) {
            Text(buttonText)
        }
    }
}
