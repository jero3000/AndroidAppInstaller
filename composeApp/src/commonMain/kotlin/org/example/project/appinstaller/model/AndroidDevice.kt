package org.example.project.appinstaller.model

data class AndroidDevice(val serial: String, val name: String){
    val label: String
        get() = "$name ($serial)"
}