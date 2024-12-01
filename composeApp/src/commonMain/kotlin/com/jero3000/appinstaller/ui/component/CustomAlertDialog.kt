package com.jero3000.appinstaller.ui.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable

@Composable
fun CustomAlertDialog(title : String?, text: String, buttonText: String, onDismiss : () -> Unit, onConfirmation : () -> Unit) {
    CustomAlertDialog(title, { Text(text) }, buttonText, onDismiss, onConfirmation)
}

@Composable
fun CustomAlertDialog(title : String?, text: @Composable (() -> Unit)?, buttonText: String, onDismiss : () -> Unit, onConfirmation : () -> Unit) {
    AlertDialog(
        onDismissRequest ={ onDismiss() },
        confirmButton = {
            Button(onClick = onConfirmation){
                Text(buttonText)
            }
        },
        title = title?.let{ { Text(title) } },
        text = text
    )
}
