package com.jero3000.appinstaller.model

data class AndroidDevice(
    val serial: String,
    val name: String,
    val manufacturer: String,
    val isHardcoded: Boolean = false
){
    val label: String
        get() = if(serial.isEmpty()) name else "$name ($serial)"
}
