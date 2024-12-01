package com.jero3000.appinstaller.platform.device

import com.jero3000.appinstaller.model.AppPackage

interface Device {

    enum class InstallMode(val key: String){
        NORMAL("normal"),

        /**
         * Allows version downgrade keeping the application data
         */
        DOWNGRADE("downgrade"),

        /**
         * Uninstall the app and data if present, then performs a clean install
         */
        CLEAN("clean")
    }

    suspend fun getSerial(): Result<String>
    suspend fun getManufacturer(): Result<String>
    suspend fun getModel(): Result<String>
    suspend fun install(app: AppPackage, mode: InstallMode = InstallMode.NORMAL): Result<Unit>
}
