package com.jero3000.appinstaller.ui.screen

object Application{
    var onExit: (() -> Unit) = {}
    fun exit() = onExit()
}
