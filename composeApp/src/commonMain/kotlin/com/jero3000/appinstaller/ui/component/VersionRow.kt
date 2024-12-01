package com.jero3000.appinstaller.ui.component

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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp

@Stable
class VersionState(major: String, minor: String, micro: String, build: String){

    var major by mutableStateOf(major)
    var minor by mutableStateOf(minor)
    var micro by mutableStateOf(micro)
    var build by mutableStateOf(build)

    val versionValid: Boolean
        get() =
            major.isNotBlank() && major.toIntOrNull()?.let { it >= 0 } ?: false &&
                    minor.isNotBlank() && minor.toIntOrNull()?.let { it >= 0 } ?: false &&
                    micro.isNotBlank() && micro.toIntOrNull()?.let { it >= 0 } ?: false &&
                    (build.isBlank() || build.toIntOrNull()?.let { it >= 0 } ?: false)

}

@Composable
fun rememberVersionState(major: String, minor: String, micro: String, build: String) = remember { VersionState(major, minor, micro, build) }

@Composable
fun rememberVersionState() = remember { VersionState("", "", "", "") }

@Composable
fun VersionRow(modifier: Modifier = Modifier, versionState: VersionState = rememberVersionState(), onVersionEntered: () -> Unit = {}){
    Row(modifier = modifier) {
        TextField(
            modifier = Modifier.width(80.dp).onFocusChanged {
                if(!it.isFocused && versionState.versionValid){
                    onVersionEntered()
                }
            },
            value = versionState.major,
            onValueChange = { value ->
                versionState.major = value
            },
            isError = !versionState.versionValid,
            label = { Text("Major", style = MaterialTheme.typography.labelSmall) },
            singleLine = true
        )
        TextField(
            modifier = Modifier.width(80.dp).padding(start = 10.dp).onFocusChanged {
                if(!it.isFocused && versionState.versionValid){
                    onVersionEntered()
                }
            },
            value = versionState.minor,
            onValueChange = { value ->
                versionState.minor = value
            },
            isError = !versionState.versionValid,
            label = { Text("Minor", style = MaterialTheme.typography.labelSmall) },
            singleLine = true
        )
        TextField(
            modifier = Modifier.width(80.dp).padding(start = 10.dp).onFocusChanged {
                if(!it.isFocused && versionState.versionValid){
                    onVersionEntered()
                }
            },
            value = versionState.micro,
            onValueChange = { value ->
                versionState.micro = value
            },
            isError = !versionState.versionValid,
            label = { Text("Micro", style = MaterialTheme.typography.labelSmall) },
            singleLine = true
        )
        TextField(
            modifier = Modifier.width(80.dp).padding(start = 10.dp).onFocusChanged {
                if(!it.isFocused && versionState.versionValid){
                    onVersionEntered()
                }
            },
            value = versionState.build,
            onValueChange = { value ->
                versionState.build = value
            },
            isError = !versionState.versionValid,
            label = { Text("Build", style = MaterialTheme.typography.labelSmall) },
            singleLine = true
        )
    }
}

