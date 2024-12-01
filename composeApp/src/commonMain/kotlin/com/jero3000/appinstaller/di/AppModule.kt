package com.jero3000.appinstaller.di

import com.jero3000.appinstaller.domain.di.domainModule
import com.jero3000.appinstaller.repository.di.repositoryModule
import com.jero3000.appinstaller.ui.di.uiModule
import org.koin.dsl.module

val appModule = module {
    includes(domainModule, repositoryModule, uiModule)
}
