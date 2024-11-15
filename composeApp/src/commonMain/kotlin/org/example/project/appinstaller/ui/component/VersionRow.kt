package org.example.project.appinstaller.ui.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Stable
class VersionState{
    var major by mutableStateOf("")
    var minor by mutableStateOf("")
    var micro by mutableStateOf("")
    var build by mutableStateOf("")
}

@Composable
fun rememberVersionState() = remember { VersionState() }

@Composable
fun VersionRow(modifier: Modifier = Modifier, versionState: VersionState = rememberVersionState()){
    Row(modifier = modifier) {
        var valid by remember { mutableStateOf(true) }
        TextField(
            modifier = Modifier.width(80.dp),
            value = versionState.major,
            onValueChange = { value ->
                versionState.major = value
                valid = value.isEmpty() || value.toIntOrNull()?.let { it >= 0 } ?: false
            },
            isError = !valid,
            label = { Text("Major", style = MaterialTheme.typography.labelSmall) },
            singleLine = true
        )
        TextField(
            modifier = Modifier.width(80.dp).padding(start = 10.dp),
            value = versionState.minor,
            onValueChange = { value ->
                versionState.minor = value
                valid = value.isEmpty() || value.toIntOrNull()?.let { it >= 0 } ?: false
            },
            isError = !valid,
            label = { Text("Minor", style = MaterialTheme.typography.labelSmall) },
            singleLine = true
        )
        TextField(
            modifier = Modifier.width(80.dp).padding(start = 10.dp),
            value = versionState.micro,
            onValueChange = { value ->
                versionState.micro = value
                valid = value.isEmpty() || value.toIntOrNull()?.let { it >= 0 } ?: false
            },
            isError = !valid,
            label = { Text("Micro", style = MaterialTheme.typography.labelSmall) },
            singleLine = true
        )
        TextField(
            modifier = Modifier.width(80.dp).padding(start = 10.dp),
            value = versionState.build,
            onValueChange = { value ->
                versionState.build = value
                valid = value.isEmpty() || value.toIntOrNull()?.let { it >= 0 } ?: false
            },
            isError = !valid,
            label = { Text("Build", style = MaterialTheme.typography.labelSmall) },
            singleLine = true
        )
    }
}