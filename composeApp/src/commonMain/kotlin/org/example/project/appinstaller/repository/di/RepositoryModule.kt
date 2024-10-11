package org.example.project.appinstaller.repository.di

import kotlinx.coroutines.Dispatchers
import org.example.project.appinstaller.platform.di.platformModule
import org.example.project.appinstaller.repository.ConfigurationDataSource
import org.example.project.appinstaller.repository.ConfigurationRepository
import org.example.project.appinstaller.repository.local.ConfigurationLocalDataSource
import org.example.project.appinstaller.repository.ConfigurationRepositoryImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val repositoryModule = module {
    includes(platformModule)

    factory<ConfigurationDataSource> { ConfigurationLocalDataSource(Dispatchers.IO, get()) }
    singleOf(::ConfigurationRepositoryImpl) { bind<ConfigurationRepository>() }
}
