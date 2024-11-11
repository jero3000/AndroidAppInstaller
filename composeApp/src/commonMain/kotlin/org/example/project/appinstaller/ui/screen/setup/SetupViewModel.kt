package org.example.project.appinstaller.ui.screen.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.appinstaller.domain.GetAppConfigFlowUseCase
import org.example.project.appinstaller.domain.GetAppConfigUseCase
import org.example.project.appinstaller.domain.GetPackageFileUseCase
import org.example.project.appinstaller.domain.ResolvePackageUrlUseCase
import org.example.project.appinstaller.domain.StoreCredentialsUseCase
import org.example.project.appinstaller.model.BuildVariant
import org.example.project.appinstaller.model.exception.CredentialsRequiredException
import org.example.project.appinstaller.ui.screen.setup.model.SetupEvent
import org.example.project.appinstaller.ui.screen.setup.model.SetupPackage
import org.example.project.appinstaller.ui.screen.setup.model.SetupState
import org.example.project.appinstaller.ui.screen.setup.model.SetupVersion

class SetupViewModel(
    private val getAppConfigFlow : GetAppConfigFlowUseCase,
    private val getAppConfig : GetAppConfigUseCase,
    private val resolveUrl: ResolvePackageUrlUseCase,
    private val getPackageFile: GetPackageFileUseCase,
    private val storeCredential: StoreCredentialsUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(SetupState())
    val uiState: StateFlow<SetupState> = _uiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SetupState()
    )

    init{
        viewModelScope.launch {
            getAppConfigFlow().collect{ appConfigResult ->
                appConfigResult.getOrNull()?.let { appConfig ->
                    _uiState.update { _ ->
                        SetupState(appConfig.projects.map { it.name })
                    }
                } ?: run {
                    //Notify the error loading the app config
                    appConfigResult.exceptionOrNull()?.stackTraceToString()?.let { error ->
                        _uiState.update { currentState ->
                            currentState.copy(error = SetupState.Error.GenericError(error))
                        }
                    }
                }
            }
        }
    }

    fun onEvent(event: SetupEvent) {
        when(event){
            is SetupEvent.OnDownloadClicked -> {
                println("Init download for version R${event.version.major}.${event.version.minor}.${event.version.micro}(${event.version.build})")
                _uiState.update { it.copy(selectedVersion = event.version) }

                viewModelScope.launch {
                    startDownload(event.version)
                }
            }
            is SetupEvent.OnProjectSelected -> {
                selectProject(event.selected)
            }
            is SetupEvent.OnSetupPackageChanged -> {
                updatePackage(event.packageName, event.checked)
            }
            is SetupEvent.OnTargetSelected -> {
                selectTarget(event.selected)
            }
            is SetupEvent.OnErrorAck -> {
                _uiState.update { it.copy(error = null) }
            }

            is SetupEvent.OnNewCredential -> viewModelScope.launch{
                storeCredential(event.host, event.credential)
                _uiState.value.selectedVersion?.let {
                    startDownload(it)
                }
            }
        }
    }

    private suspend fun startDownload(version: SetupVersion) {
        val variant = getBuildVariant()
        val placeHolders = mutableMapOf(
            ResolvePackageUrlUseCase.MAJOR_PLACEHOLDER to version.major,
            ResolvePackageUrlUseCase.MINOR_PLACEHOLDER to version.minor,
            ResolvePackageUrlUseCase.MICRO_PLACEHOLDER to version.micro
        )
        version.build?.let { build ->
            placeHolders.put(ResolvePackageUrlUseCase.BUILD_PLACEHOLDER, build)
        }

        _uiState.value.packages.filter { it.selected }.forEach{ app ->
            val appPackage = variant.packages.first{it.packageName == app.packageName}
            val url = resolveUrl(variant, appPackage, placeHolders)
            updatePackage(app.packageName, SetupPackage.State.Downloading)
            val result = getPackageFile(url)
            if(result.isSuccess){
                appPackage.packageFile = result.getOrNull()
                updatePackage(app.packageName, SetupPackage.State.Downloaded)
            } else {
                updatePackage(app.packageName, SetupPackage.State.Error)
                result.exceptionOrNull()?.let {
                    processDownloadException(it)
                }
            }
        }
    }

    private fun processDownloadException(exception: Throwable){
        if(exception is CredentialsRequiredException){
            _uiState.update { currentState ->
                currentState.copy(error = SetupState.Error.CredentialsRequired(exception.host))
            }
        } else {
            _uiState.update { currentState ->
                currentState.copy(error = SetupState.Error.GenericError(exception.stackTraceToString()))
            }
        }
    }

    private fun selectProject(selected: String) = viewModelScope.launch {
        getAppConfig()?.let { appConfig ->
            _uiState.update { currentState ->
                val project = appConfig.projects.find { it.name == selected }!!
                currentState.copy(
                    selectedProject = selected,
                    targets = project.buildVariants.map { it.name }
                )
            }
        }
    }

    private fun selectTarget(selected: String) = viewModelScope.launch {
        getAppConfig()?.let { appConfig ->
            _uiState.update { currentState ->
                val project = appConfig.projects.find { it.name == currentState.selectedProject }!!
                val variant = project.buildVariants.find { it.name == selected }
                currentState.copy(
                    selectedTarget = selected,
                    packages = variant!!.packages.map { SetupPackage(it.name, it.packageName) }
                )
            }
        }
    }

    private fun updatePackage(packageName: String, checked: Boolean){
        _uiState.update { currentState ->
            currentState.copy(packages = currentState.packages.map {
                if(it.packageName == packageName){
                    it.copy(selected = checked)
                } else {
                    it
                }
            })
        }
    }

    private fun updatePackage(packageName: String, state: SetupPackage.State){
        _uiState.update { currentState ->
            currentState.copy(packages = currentState.packages.map {
                if(it.packageName == packageName){
                    it.copy(state = state)
                } else {
                    it
                }
            })
        }
    }

    private fun getBuildVariant(): BuildVariant {
        val appConfig = getAppConfig()!!
        val project = appConfig.projects.first{ it.name == _uiState.value.selectedProject}
        return project.buildVariants.first { it.name ==  _uiState.value.selectedTarget }
    }
}