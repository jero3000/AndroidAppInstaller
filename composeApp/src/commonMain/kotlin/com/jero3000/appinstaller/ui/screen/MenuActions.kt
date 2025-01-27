package com.jero3000.appinstaller.ui.screen

import dev.zwander.kotlin.file.filekit.toKmpFile
import io.github.vinceglb.filekit.core.FileKit
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PickerType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import com.jero3000.appinstaller.domain.LoadAppConfigUseCase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object MenuActions : KoinComponent{
    fun loadConfiguration(coroutineScope: CoroutineScope){
        val loadFile : LoadAppConfigUseCase by inject()
        coroutineScope.launch {
            FileKit.pickFile(
                type = PickerType.File(listOf("json")),
                mode = PickerMode.Single,
                title = "Select a JSON configuration file",
            )?.toKmpFile()?.let {
                loadFile(it)
            }
        }
    }

    fun loadConfiguration(path: String){
        val loadFile : LoadAppConfigUseCase by inject()
        loadFile(path)
    }
}