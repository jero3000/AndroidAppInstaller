package com.jero3000.appinstaller.ui.screen.setup.model

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

@Stable
data class SetupPlaceholder(
    val id : String,
    val name: String,
    var initialSelected : Boolean =  false
){
    var checked by mutableStateOf(initialSelected)
}
