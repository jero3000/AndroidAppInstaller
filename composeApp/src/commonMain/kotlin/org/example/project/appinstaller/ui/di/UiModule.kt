package org.example.project.appinstaller.ui.di

import org.example.project.appinstaller.ui.screen.setup.SetupViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val uiModule = module {
    viewModelOf(::SetupViewModel)
}