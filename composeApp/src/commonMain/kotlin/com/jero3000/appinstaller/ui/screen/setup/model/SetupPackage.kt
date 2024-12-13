package com.jero3000.appinstaller.ui.screen.setup.model

data class SetupPackage(
    val name: String,
    val packageName: String,
    val state: State = State.Idle,
    val selected: Boolean = false
){
    enum class State {
        Idle,
        Downloading,
        Downloaded,
        Installing,
        Installed,
        Error
    }
}

