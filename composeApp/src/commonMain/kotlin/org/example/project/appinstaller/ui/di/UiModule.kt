package org.example.project.appinstaller.ui.di

import org.example.project.appinstaller.ui.screen.settings.SettingsScreenModel
import org.example.project.appinstaller.ui.screen.setup.SetupViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val uiModule = module {
    viewModelOf(::SetupViewModel)
    factoryOf(::SettingsScreenModel)
}