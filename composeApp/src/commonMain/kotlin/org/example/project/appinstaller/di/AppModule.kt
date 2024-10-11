package org.example.project.appinstaller.di

import org.example.project.appinstaller.domain.di.domainModule
import org.example.project.appinstaller.repository.di.repositoryModule
import org.example.project.appinstaller.ui.di.uiModule
import org.koin.core.module.Module
import org.koin.dsl.module

val appModule = module {
    includes(platformModule, domainModule, repositoryModule, uiModule)
}

expect val platformModule: Module