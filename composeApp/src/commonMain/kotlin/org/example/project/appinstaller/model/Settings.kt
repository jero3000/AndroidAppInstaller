package org.example.project.appinstaller.model

enum class Settings(val key: String) {
    INSTALL_MODE("install_mode"),
    ADB_HOST("adb_host"),
    ADB_PORT("adb_port")
}

object Defaults{
    const val ADB_HOST = "localhost"
    const val ADB_PORT = 5037
}
