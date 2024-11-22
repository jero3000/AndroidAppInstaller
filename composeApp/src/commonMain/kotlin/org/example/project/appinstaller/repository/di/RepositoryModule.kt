package org.example.project.appinstaller.repository.di

import kotlinx.coroutines.Dispatchers
import org.example.project.appinstaller.platform.di.platformModule
import org.example.project.appinstaller.repository.adb.AdbRepository
import org.example.project.appinstaller.repository.adb.AdbRepositoryImpl
import org.example.project.appinstaller.repository.config.datasource.ConfigurationDataSource
import org.example.project.appinstaller.repository.config.ConfigurationRepository
import org.example.project.appinstaller.repository.config.datasource.ConfigurationLocalDataSource
import org.example.project.appinstaller.repository.config.ConfigurationRepositoryImpl
import org.example.project.appinstaller.repository.credential.CredentialRepository
import org.example.project.appinstaller.repository.credential.CredentialRepositoryImpl
import org.example.project.appinstaller.repository.credential.datasource.CredentialDataSource
import org.example.project.appinstaller.repository.credential.datasource.CredentialPreferencesDataSource
import org.example.project.appinstaller.repository.file.FileRepository
import org.example.project.appinstaller.repository.file.FileRepositoryImpl
import org.example.project.appinstaller.repository.file.datasource.FileDataSource
import org.example.project.appinstaller.repository.file.datasource.FileDataSourceResolver
import org.example.project.appinstaller.repository.preferences.ApplicationPreferences
import org.example.project.appinstaller.repository.preferences.ApplicationPreferencesImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.withOptions

import org.koin.dsl.module
import org.koin.core.module.Module
import org.koin.core.module.dsl.named
import org.koin.core.qualifier.named


val repositoryModule = module {
    includes(platformModule, systemDataSourceModule)

    factory<ConfigurationDataSource> { ConfigurationLocalDataSource(Dispatchers.IO, get(), get(), get()) }
    singleOf(::ConfigurationRepositoryImpl) { bind<ConfigurationRepository>() }

    factory<FileDataSource> {
        FileDataSourceResolver(listOf(
            get<FileDataSource>(named("ftp"))
        ))
    } withOptions {
        named("resolver")
    }
    single<FileRepository> { FileRepositoryImpl(get<FileDataSource>(named("resolver")), get(), get(), get(), get()) }

    factory<CredentialDataSource> { CredentialPreferencesDataSource(get(), Dispatchers.IO) }
    single<CredentialRepository> { CredentialRepositoryImpl(get()) }

    single<ApplicationPreferences> { ApplicationPreferencesImpl("com.jero3000.appinstaller.preferences", get(), Dispatchers.IO) }
    singleOf(::AdbRepositoryImpl) { bind<AdbRepository>() }
}

expect val systemDataSourceModule: Module
