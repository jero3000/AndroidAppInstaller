package org.example.project.appinstaller.ui.screen.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.appinstaller.domain.DiscoverDevicesUseCase
import org.example.project.appinstaller.domain.GetAppConfigFlowUseCase
import org.example.project.appinstaller.domain.GetAppConfigUseCase
import org.example.project.appinstaller.domain.GetPackageFileUseCase
import org.example.project.appinstaller.domain.InstallAppPackageUseCase
import org.example.project.appinstaller.domain.ResolvePackageUrlUseCase
import org.example.project.appinstaller.domain.StoreCredentialsUseCase
import org.example.project.appinstaller.model.BuildVariant
import org.example.project.appinstaller.model.exception.CredentialsRequiredException
import org.example.project.appinstaller.repository.preferences.ApplicationPreferences
import org.example.project.appinstaller.ui.screen.setup.model.SetupEvent
import org.example.project.appinstaller.ui.screen.setup.model.SetupPackage
import org.example.project.appinstaller.ui.screen.setup.model.SetupState
import org.example.project.appinstaller.ui.screen.setup.model.SetupVersion

class SetupViewModel(
    private val getAppConfigFlow : GetAppConfigFlowUseCase,
    private val getAppConfig : GetAppConfigUseCase,
    private val resolveUrl: ResolvePackageUrlUseCase,
    private val getPackageFile: GetPackageFileUseCase,
    private val storeCredential: StoreCredentialsUseCase,
    private val preferences: ApplicationPreferences,
    private val discoverDevices: DiscoverDevicesUseCase,
    private val installPackage: InstallAppPackageUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(SetupState())
    val uiState: StateFlow<SetupState> = _uiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SetupState()
    )

    init{
        viewModelScope.launch { //Handles the app configuration loading
            getAppConfigFlow().collect{ appConfigResult ->
                appConfigResult.getOrNull()?.let { appConfig ->
                    _uiState.update { _ ->
                        SetupState(appConfig.projects.map { it.name })
                    }
                    readPreferences()
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
        viewModelScope.launch { //Handles the Android device discovery
            discoverDevices().collect{ devices ->
                _uiState.update { it.copy(devices = devices) }
            }
        }
    }

    fun onEvent(event: SetupEvent) {
        when(event){
            is SetupEvent.OnDownloadClicked -> {
                println("Init download for version R${uiState.value.selectedVersion?.major}." +
                        "${uiState.value.selectedVersion?.minor}." +
                        "${uiState.value.selectedVersion?.micro}" +
                        "(${uiState.value.selectedVersion?.build})")

                viewModelScope.launch {
                    startDownload(uiState.value.selectedVersion!!)
                    storePreferences()
                }
            }
            is SetupEvent.OnInstall -> viewModelScope.launch {
                startInstall()
            }
            is SetupEvent.OnProjectSelected -> {
                selectProject(event.selected)
            }
            is SetupEvent.OnVersionEntered -> {
                _uiState.update { it.copy(selectedVersion = event.version) }
            }
            is SetupEvent.OnSetupPackageChanged -> {
                updatePackage(event.packageName, event.checked)
            }
            is SetupEvent.OnTargetSelected -> {
                selectTarget(event.selected)
            }
            is SetupEvent.OnDeviceSelected -> {
                _uiState.update { it.copy(selectedDevice = event.selected) }
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
            ResolvePackageUrlUseCase.MAJOR_PLACEHOLDER to version.major!!,
            ResolvePackageUrlUseCase.MINOR_PLACEHOLDER to version.minor!!,
            ResolvePackageUrlUseCase.MICRO_PLACEHOLDER to version.micro!!
        )
        version.build?.let { build ->
            placeHolders.put(ResolvePackageUrlUseCase.BUILD_PLACEHOLDER, build)
        }

        val packagesSelected = _uiState.value.packages.filter { it.selected }
        for(app in packagesSelected){
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
                break
            }
        }
    }

    private suspend fun startInstall() {
        val variant = getBuildVariant()
        val device = uiState.value.selectedDevice!!
        val packagesSelected = _uiState.value.packages.filter { it.selected }
        for(app in packagesSelected){
            println("Installing ${app.name}")
            val appPackage = variant.packages.first{it.packageName == app.packageName}
            updatePackage(app.packageName, SetupPackage.State.Installing)
            val result = installPackage(device.serial, appPackage)
            if(result.isSuccess){
                updatePackage(app.packageName, SetupPackage.State.Installed)
            } else {
                updatePackage(app.packageName, SetupPackage.State.Error)
                result.exceptionOrNull()?.let { notifyGenericError(it) }
                break
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

    private fun selectProject(selected: String) {
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

    private fun selectTarget(selected: String) {
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

    private suspend fun storePreferences(){
        _uiState.value.selectedProject?.let { preferences.putString(PROJECT_KEY, it) }
        _uiState.value.selectedTarget?.let { preferences.putString(VARIANT_KEY, it) }
        _uiState.value.selectedVersion?.let {
            val build = it.build ?: ""
            preferences.putString(VERSION_KEY, "${it.major}.${it.minor}.${it.micro}.${build}")
        }
    }

    private suspend fun readPreferences(){
        getAppConfig()?.let { appConfig ->
            println("Reading preferences...")
            val project = preferences.getString(PROJECT_KEY)?.takeIf { project -> appConfig.projects.map { it.name }.contains(project) }
            println("project: $project")
            val variant = if(project != null){
                preferences.getString(VARIANT_KEY)?.takeIf { variant -> appConfig.projects.first{ it.name == project }.buildVariants.map { it.name }.contains(variant) }
            } else null
            val version = preferences.getString(VERSION_KEY)?.split(".")?.let { version ->
                SetupVersion(version[0], version[1], version[2], version[3].takeIf { it.isNotEmpty() })
            }
            project?.let { selectProject(it) }
            variant?.let { selectTarget(it) }
            _uiState.update { it.copy(selectedVersion = version) }
        }
    }

    companion object{
        private const val PROJECT_KEY = "project"
        private const val VARIANT_KEY = "variant"
        private const val VERSION_KEY = "version"
    }
}