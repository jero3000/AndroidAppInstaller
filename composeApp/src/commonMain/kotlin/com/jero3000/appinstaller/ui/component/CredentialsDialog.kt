package com.jero3000.appinstaller.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun CredentialsDialog(host: String,
                      onConfirmation: (user: String, pass: String) -> Unit,
                      onCancel: ()-> Unit){
    var user by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest ={},
        content = {
            Box(
                modifier = Modifier.background(Color.White, RoundedCornerShape(5.dp))
            ){
                Column{
                    Text("Credentials required",
                        modifier = Modifier.padding(top = 24.dp, start = 24.dp),
                        style = MaterialTheme.typography.titleMedium)
                    Text("Credentials required for $host:",
                        modifier = Modifier.padding(top = 8.dp, start = 24.dp, end = 24.dp),
                        style = MaterialTheme.typography.bodyMedium )
                    TextField(
                        modifier = Modifier.width(230.dp)
                            .padding(top = 10.dp)
                            .align(Alignment.CenterHorizontally),
                        value = user,
                        onValueChange = { value ->
                            user = value
                        },
                        label = { Text("User", style = MaterialTheme.typography.labelSmall) },
                        singleLine = true
                    )
                    TextField(
                        modifier = Modifier.width(230.dp)
                            .padding(top = 10.dp)
                            .align(Alignment.CenterHorizontally),
                        value = password,
                        onValueChange = { value ->
                            password = value
                        },
                        label = { Text("Password", style = MaterialTheme.typography.labelSmall) },
                        singleLine = true,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            val image = if (passwordVisible)
                                Icons.Filled.Visibility
                            else Icons.Filled.VisibilityOff

                            // Please provide localized description for accessibility services
                            val description = if (passwordVisible) "Hide password" else "Show password"

                            IconButton(onClick = {passwordVisible = !passwordVisible}){
                                Icon(imageVector  = image, description)
                            }
                        }
                    )
                    Row(modifier = Modifier.padding(top = 10.dp, end = 24.dp, bottom = 24.dp).align(
                        Alignment.End)){
                        Button(modifier = Modifier.padding(end = 10.dp),
                            onClick = { onConfirmation(user, password) }){
                            Text("Ok")
                        }
                        Button(onClick = onCancel){
                            Text("Cancel")
                        }
                    }
                }
            }
        }
    )
}
