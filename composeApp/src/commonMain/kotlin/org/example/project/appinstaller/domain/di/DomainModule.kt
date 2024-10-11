package org.example.project.appinstaller.domain.di

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import org.example.project.appinstaller.domain.GetAppConfigUseCase

val domainModule = module {
    factoryOf(::GetAppConfigUseCase)
}
