package org.example.project.appinstaller.domain.di

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import org.example.project.appinstaller.domain.GetAppConfigUseCase
import org.example.project.appinstaller.domain.GetPackageFileUseCase
import org.example.project.appinstaller.domain.ResolvePackageUrlUseCase

val domainModule = module {
    factoryOf(::GetAppConfigUseCase)
    factoryOf(::GetPackageFileUseCase)
    factoryOf(::ResolvePackageUrlUseCase)
}
