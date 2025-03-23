package com.jero3000.appinstaller.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

data class DropDownItem(val header: Boolean, val name: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDownRow(modifier: Modifier = Modifier,
                   label : String,
                   options: List<DropDownItem>,
                   default: String,
                   onSelected: (option: String) -> Unit){
    Row (modifier) {
        var expanded by remember { mutableStateOf(false) }
        var text by remember(default) { mutableStateOf(default) }
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
        ) {
            TextField(
                // The `menuAnchor` modifier must be passed to the text field to handle
                // expanding/collapsing the menu on click. A read-only text field has
                // the anchor type `PrimaryNotEditable`.
                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable),
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
                    if(option.header){
                        DropdownMenuItem(
                            modifier = Modifier.background(MaterialTheme.colorScheme.background),
                            text = { Text("Hardcoded devices") },
                            onClick = {},
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                            enabled = false

                        )
                    } else {
                        DropdownMenuItem(
                            text = { Text(option.name, style = MaterialTheme.typography.labelMedium) },
                            onClick = {
                                text = option.name
                                onSelected(option.name)
                                expanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                    }
                }
            }
        }
    }
}