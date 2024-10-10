package org.example.project.appinstaller.ui.screen.setup.model

data class SetupPackage(
    val name: String,
    val packageName: String,
    val state: State = State.Idle,
    val selected: Boolean = true
){
    enum class State {
        Idle,
        Downloading,
        Downloaded,
        Installing,
        Installed
    }
}

