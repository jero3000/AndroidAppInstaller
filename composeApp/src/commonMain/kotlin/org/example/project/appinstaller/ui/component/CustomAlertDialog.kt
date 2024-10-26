package org.example.project.appinstaller.ui.component

import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable

@Composable
fun CustomAlertDialog(text: String, onDismiss : () -> Unit) {
    AlertDialog(
        onDismissRequest ={},
        confirmButton = {
            Button(onClick = onDismiss){
                Text("Ok")
            }
        },
        title = { Text("An error has occurred") },
        text = { Text(text) }
    )
}
