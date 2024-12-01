package com.jero3000.appinstaller.ui.screen.setup.model

import com.jero3000.appinstaller.model.AndroidDevice
import com.jero3000.appinstaller.model.AppVersion

data class SetupState(
    val isLoading: Boolean = true,
    val projects : List<String> = emptyList(),
    val selectedProject: String? = null,
    val targets : List<String> = emptyList(),
    val selectedTarget: String? = null,
    val selectedVersion: AppVersion? = null,
    val devices : List<AndroidDevice> = emptyList(),
    val selectedDevice: AndroidDevice? = null,
    val packages : List<SetupPackage> = emptyList(),
    val error: Error? = null
){
    sealed interface Error{
        data class GenericError(val description: String): Error
        data class CredentialsRequired(val host: String): Error
        data object AdbBinaryNotFound: Error
    }
}
