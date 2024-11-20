package org.example.project.appinstaller.ui.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable

@Composable
fun CustomAlertDialog(title : String?, text: String?, onDismiss : () -> Unit, onConfirmation : () -> Unit) {
    AlertDialog(
        onDismissRequest ={ onDismiss() },
        confirmButton = {
            Button(onClick = onConfirmation){
                Text("Ok")
            }
        },
        title = title?.let{ { Text(title) } },
        text = text?.let{ { Text(text) } }
    )
}
