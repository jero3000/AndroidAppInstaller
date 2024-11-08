package org.example.project.appinstaller.ui.screen.setup.model

data class SetupState(
    val projects : List<String> = emptyList(),
    val selectedProject: String? = null,
    val targets : List<String> = emptyList(),
    val selectedTarget: String? = null,
    val selectedVersion: SetupVersion? = null,
    val packages : List<SetupPackage> = emptyList(),
    val error: Error? = null
){
    sealed interface Error{
        data class GenericError(val description: String): Error
        data class CredentialsRequired(val host: String): Error
    }
}
