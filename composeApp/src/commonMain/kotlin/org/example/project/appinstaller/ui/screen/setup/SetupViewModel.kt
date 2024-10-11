package org.example.project.appinstaller.ui.screen.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.appinstaller.domain.GetAppConfigUseCase
import org.example.project.appinstaller.model.AppConfig
import org.example.project.appinstaller.ui.screen.setup.model.SetupEvent
import org.example.project.appinstaller.ui.screen.setup.model.SetupPackage
import org.example.project.appinstaller.ui.screen.setup.model.SetupState

class SetupViewModel(
    private val getAppConfig : GetAppConfigUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(SetupState())
    val uiState: StateFlow<SetupState> = _uiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SetupState()
    )

    init{
        viewModelScope.launch {
            val appConfigResult = getAppConfig()
            appConfigResult.getOrNull()?.let { appConfig ->
                _uiState.update { _ ->
                    SetupState(appConfig.projects.map { it.name })
                }
            } ?: run {
                _uiState.update { currentState ->
                    currentState.copy(error = appConfigResult.exceptionOrNull()?.stackTraceToString())
                }
            }
        }
    }

    fun onEvent(event: SetupEvent) {
        when(event){
            is SetupEvent.OnDownloadClicked -> {
                println("Init download for version R${event.version.major}.${event.version.minor}.${event.version.micro}(${event.version.build})")
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
        }
    }

    private fun selectProject(selected: String) = viewModelScope.launch {
        val appConfig = getAppConfig().getOrNull()
        appConfig?.let {
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
        val appConfig = getAppConfig().getOrNull()
        appConfig?.let {
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
}