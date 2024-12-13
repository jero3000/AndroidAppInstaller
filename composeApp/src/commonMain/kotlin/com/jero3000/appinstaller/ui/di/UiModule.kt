package com.jero3000.appinstaller.ui.di

import com.jero3000.appinstaller.ui.screen.settings.SettingsScreenModel
import com.jero3000.appinstaller.ui.screen.setup.SetupViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val uiModule = module {
    viewModelOf(::SetupViewModel)
    factoryOf(::SettingsScreenModel)
}