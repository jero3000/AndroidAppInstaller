package com.jero3000.appinstaller.model

data class AndroidDevice(val serial: String, val name: String, val manufacturer: String){
    val label: String
        get() = "$name ($serial)"
}
