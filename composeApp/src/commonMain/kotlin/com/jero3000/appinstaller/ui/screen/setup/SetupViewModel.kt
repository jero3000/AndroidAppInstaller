package com.jero3000.appinstaller.ui.screen.setup

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jero3000.appinstaller.domain.DiscoverDevicesUseCase
import com.jero3000.appinstaller.domain.EnsureAdbServerRunningUseCase
import com.jero3000.appinstaller.domain.FetchConfigurationUseCase
import com.jero3000.appinstaller.domain.GetAppConfigFlowUseCase
import com.jero3000.appinstaller.domain.GetAppConfigUseCase
import com.jero3000.appinstaller.domain.GetPackageFileUseCase
import com.jero3000.appinstaller.domain.InstallAppPackageUseCase
import com.jero3000.appinstaller.domain.ResolvePackageUrlUseCase
import com.jero3000.appinstaller.domain.StoreCredentialsUseCase
import com.jero3000.appinstaller.model.AppConfig
import com.jero3000.appinstaller.model.AppVersion
import com.jero3000.appinstaller.model.BuildVariant
import com.jero3000.appinstaller.model.Settings
import com.jero3000.appinstaller.model.exception.CredentialsRequiredException
import com.jero3000.appinstaller.repository.preferences.ApplicationPreferences
import com.jero3000.appinstaller.ui.screen.setup.model.SetupEvent
import com.jero3000.appinstaller.ui.screen.setup.model.SetupPackage
import com.jero3000.appinstaller.ui.screen.setup.model.SetupPlaceholder
import com.jero3000.appinstaller.ui.screen.setup.model.SetupState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.TimeoutException
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class SetupViewModel(
    private val getAppConfigFlow : GetAppConfigFlowUseCase,
    private val getAppConfig : GetAppConfigUseCase,
    private val resolveUrl: ResolvePackageUrlUseCase,
    private val getPackageFile: GetPackageFileUseCase,
    private val storeCredential: StoreCredentialsUseCase,
    private val preferences: ApplicationPreferences,
    private val discoverDevices: DiscoverDevicesUseCase,
    private val installPackage: InstallAppPackageUseCase,
    private val ensureAdbServerRunning: EnsureAdbServerRunningUseCase,
    private val fetchAppConfig: FetchConfigurationUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(SetupState())
    val uiState: StateFlow<SetupState> = _uiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SetupState()
    )
    private val _placeholders = SnapshotStateList<SetupPlaceholder>()
    val placeholders : List<SetupPlaceholder>
        get() = _placeholders
    private var scanJob : Job? = null
    private var configurationWaitCondition: Continuation<Unit>? = null
    private var configurationLoaded = false

    init {
        viewModelScope.launch { //Handles the app configuration loading
            _uiState.update { it.copy(isLoading = fetchAppConfig().isSuccess) }
            getAppConfigFlow().collect{ appConfigResult ->
                appConfigResult.getOrNull()?.let { appConfig ->
                    _uiState.update { _ ->
                        SetupState(
                            projects = appConfig.projects.map { it.name },
                            devices = appConfig.devices
                        )
                    }
                    _placeholders.apply {
                        clear()
                        addAll(appConfig.placeholders.map { SetupPlaceholder(it.id, it.name) })
                    }
                    readPreferences()
                    val adbError = ensureAdbServerRunning().exceptionOrNull()?.let {
                        if(it is TimeoutException){
                            SetupState.Error.AdbServerTimeout
                        } else {
                            SetupState.Error.AdbBinaryNotFound
                        }
                    }
                    configurationLoaded =  true
                    configurationWaitCondition?.resume(Unit)
                    configurationWaitCondition = null
                    _uiState.update { it.copy(isLoading = false, error = adbError) }
                } ?: run {
                    //Notify the error loading the app config
                    appConfigResult.exceptionOrNull()?.let { notifyGenericError(it) }
                }
            }
        }
    }

    fun onEvent(event: SetupEvent) {
        when(event){
            is SetupEvent.OnStart -> {
                scanJob = viewModelScope.launch { //Handles the Android device discovery
                    if(!configurationLoaded) {
                        //Waits for configuration loaded to start the devices discovery
                        suspendCoroutine { continuation ->
                            configurationWaitCondition = continuation
                        }
                    }

                    discoverDevices().collect{ devices ->
                        val hardcodedDevices = getAppConfig()?.devices ?: emptyList()
                        _uiState.update { it.copy(devices = devices + hardcodedDevices) }
                    }
                }
            }
            is SetupEvent.OnStop -> {
                scanJob?.cancel()
            }
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
                selectProject(event.selected, getAppConfig())
            }
            is SetupEvent.OnVersionEntered -> {
                _uiState.update { it.copy(selectedVersion = event.version) }
            }
            is SetupEvent.OnSetupPackageChanged -> {
                updatePackage(event.packageName, event.checked)
            }
            is SetupEvent.OnTargetSelected -> {
                selectTarget(event.selected, getAppConfig())
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

            is SetupEvent.OnPlaceholderChanged -> {
                _placeholders.find { it.id == event.id }?.let { placeholder ->
                    placeholder.checked = event.checked
                }
            }
        }
    }

    private suspend fun startDownload(version: AppVersion) {
        val appConfig = getAppConfig()!!
        val variant = getBuildVariant(appConfig)
        val packagesSelected = _uiState.value.packages.filter { it.selected }
        val deviceManufacturer = uiState.value.selectedDevice!!.manufacturer

        for(app in packagesSelected){
            val appPackage = variant.packages.first{it.packageName == app.packageName}
            //Provide only the checked placeholders
            val placeholders = appConfig.placeholders.filter { placeholder ->
                _placeholders.find { it.id == placeholder.id }?.checked == true
            }
            val url = resolveUrl(variant, appPackage, version, deviceManufacturer, placeholders)
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
        val variant = getBuildVariant(getAppConfig()!!)
        val device = uiState.value.selectedDevice!!
        val packagesSelected = _uiState.value.packages.filter { it.selected }
        for(app in packagesSelected){
            println("Installing ${app.name}")
            val appPackage = variant.packages.first{it.packageName == app.packageName}
            updatePackage(app.packageName, SetupPackage.State.Installing)
            val result = installPackage(device.serial, appPackage, preferences.getString(Settings.INSTALL_MODE.key))
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
            notifyGenericError(exception)
        }
    }

    private fun selectProject(selected: String, appConfig: AppConfig?) {
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

    private fun selectTarget(selected: String, appConfig: AppConfig?) {
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

    private fun getBuildVariant(appConfig: AppConfig): BuildVariant {
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
                AppVersion(version[0], version[1], version[2], version[3].takeIf { it.isNotEmpty() })
            }
            project?.let { selectProject(it, appConfig) }
            variant?.let { selectTarget(it, appConfig) }
            _uiState.update { it.copy(selectedVersion = version) }
        }
    }

    private fun notifyGenericError(throwable: Throwable){
        println(throwable.stackTraceToString())
        _uiState.update { currentState ->
            currentState.copy(
                error = SetupState.Error.GenericError(
                    throwable.message?.let { "" + throwable.javaClass.name + ": $it" } ?: (throwable.stackTraceToString().take(170) + "...")
                )
            )
        }
    }

    companion object{
        private const val PROJECT_KEY = "project"
        private const val VARIANT_KEY = "variant"
        private const val VERSION_KEY = "version"
    }
}