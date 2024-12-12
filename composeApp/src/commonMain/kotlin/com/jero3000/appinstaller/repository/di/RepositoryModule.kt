package com.jero3000.appinstaller.repository.di

import kotlinx.coroutines.Dispatchers
import com.jero3000.appinstaller.platform.di.platformModule
import com.jero3000.appinstaller.repository.adb.AdbRepository
import com.jero3000.appinstaller.repository.adb.AdbRepositoryImpl
import com.jero3000.appinstaller.repository.config.datasource.ConfigurationDataSource
import com.jero3000.appinstaller.repository.config.ConfigurationRepository
import com.jero3000.appinstaller.repository.config.datasource.ConfigurationLocalDataSource
import com.jero3000.appinstaller.repository.config.ConfigurationRepositoryImpl
import com.jero3000.appinstaller.repository.credential.CredentialRepository
import com.jero3000.appinstaller.repository.credential.CredentialRepositoryImpl
import com.jero3000.appinstaller.repository.credential.datasource.CredentialDataSource
import com.jero3000.appinstaller.repository.credential.datasource.MemoryCredentialDataSource
import com.jero3000.appinstaller.repository.credential.datasource.PersistenceSwitcher
import com.jero3000.appinstaller.repository.credential.datasource.PreferencesCredentialDataSource
import com.jero3000.appinstaller.repository.file.FileRepository
import com.jero3000.appinstaller.repository.file.FileRepositoryImpl
import com.jero3000.appinstaller.repository.file.datasource.FileDataSource
import com.jero3000.appinstaller.repository.file.datasource.FileDataSourceResolver
import com.jero3000.appinstaller.repository.preferences.ApplicationPreferences
import com.jero3000.appinstaller.repository.preferences.ApplicationPreferencesImpl
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
            get<FileDataSource>(named("ftp")),
            get<FileDataSource>(named("s3"))
        ), get())
    } withOptions {
        named("resolver")
    }
    single<FileRepository> { FileRepositoryImpl(get<FileDataSource>(named("resolver")), get(), get(), get(), get()) }

    single<ApplicationPreferences> { ApplicationPreferencesImpl("com.jero3000.appinstaller.preferences", get(), Dispatchers.IO) }

    factory<CredentialDataSource> { PreferencesCredentialDataSource(get(), Dispatchers.IO) } withOptions {
        named("diskCredentials")
    }
    factory<CredentialDataSource> { MemoryCredentialDataSource() } withOptions {
        named("memoryCredentials")
    }
    factory<CredentialDataSource> { PersistenceSwitcher(
        diskDataSource = get<CredentialDataSource>(named("diskCredentials")),
        memoryDataSource = get<CredentialDataSource>(named("memoryCredentials")),
        preferences = get())
    } withOptions {
        named("persistenceSwitcher")
    }

    single<CredentialRepository> { CredentialRepositoryImpl(get<CredentialDataSource>(named("persistenceSwitcher"))) }

    singleOf(::AdbRepositoryImpl) { bind<AdbRepository>() }
}

expect val systemDataSourceModule: Module
