package org.example.project.appinstaller

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.TextField
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
import org.example.project.appinstaller.ui.theme.CustomTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {

    MaterialTheme {
        Column(modifier = Modifier.padding(20.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            var majorVersion by remember { mutableStateOf("") }
            var minorVersion by remember { mutableStateOf("") }
            var microVersion by remember { mutableStateOf("") }
            var buildVersion by remember { mutableStateOf("") }
            DropDownRow(
                modifier = Modifier.padding(top = 20.dp),
                label = "Project",
                options = listOf("Izzi", "Zapi"),
                default = "Not set"
            )
            DropDownRow(
                modifier = Modifier.padding(top = 20.dp),
                label = "Target",
                options = listOf("Handheld", "Leanback", "Operator Tier"),
                default = "Not set"
            )
            Row(modifier = Modifier.padding(top = 20.dp)){
                TextField(
                    modifier = Modifier.width(80.dp),
                    value = majorVersion,
                    onValueChange = { majorVersion = it },
                    label = { Text("Major", style = MaterialTheme.typography.labelSmall) },
                    singleLine = true
                )
                TextField(
                    modifier = Modifier.width(80.dp).padding(start = 10.dp),
                    value = minorVersion,
                    onValueChange = { minorVersion = it },
                    label = { Text("Minor", style = MaterialTheme.typography.labelSmall) },
                    singleLine = true
                )
                TextField(
                    modifier = Modifier.width(80.dp).padding(start = 10.dp),
                    value = microVersion,
                    onValueChange = { microVersion = it },
                    label = { Text("Micro", style = MaterialTheme.typography.labelSmall) },
                    singleLine = true
                )
                TextField(
                    modifier = Modifier.width(80.dp).padding(start = 10.dp),
                    value = buildVersion,
                    onValueChange = { buildVersion = it },
                    label = { Text("Build", style = MaterialTheme.typography.labelSmall) },
                    singleLine = true
                )
            }
            Button(
                modifier = Modifier.padding(top = 20.dp),
                colors = ButtonDefaults.buttonColors(),
                onClick = { /* Do something! */ }) {
                Text("Download")
            }
            Row {
                val apps = listOf("Launcher", "Autopair")
                Column(modifier = Modifier.wrapContentSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Row {
                        Text(modifier = Modifier.padding(top = 20.dp), text = "Apps:")
                    }
                    apps.forEach { app ->
                        AppRow(modifier = Modifier.padding(top = 10.dp).width(450.dp),
                            appName = app,
                            color = CustomTheme.colors.error,
                            checked = true,
                            state = "Idle",
                            isTransient = true)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDownRow(modifier: Modifier = Modifier, label : String, options: List<String>, default: String){
    Row (modifier) {
        var expanded by remember { mutableStateOf(false) }
        var text by remember { mutableStateOf(default) }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
        ) {
            TextField(
                // The `menuAnchor` modifier must be passed to the text field to handle
                // expanding/collapsing the menu on click. A read-only text field has
                // the anchor type `PrimaryNotEditable`.
                modifier = Modifier.menuAnchor(),
                value = text,
                textStyle = MaterialTheme.typography.labelLarge,
                onValueChange = {},
                readOnly = true,
                singleLine = true,
                label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = ExposedDropdownMenuDefaults.textFieldColors(),
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option, style = MaterialTheme.typography.labelMedium) },
                        onClick = {
                            text = option
                            expanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    )
                }
            }
        }
    }
}

@Composable
fun AppRow(modifier: Modifier = Modifier,
           appName: String,
           color: Color,
           checked: Boolean,
           state: String,
           isTransient: Boolean ){
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
                    onCheckedChange = { selected = it }
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
