package com.jero3000.appinstaller.repository.di

import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.Settings
import kotlinx.coroutines.Dispatchers
import com.jero3000.appinstaller.repository.credential.CredentialRepository
import com.jero3000.appinstaller.repository.credential.CredentialRepositoryImpl
import com.jero3000.appinstaller.repository.credential.datasource.CredentialDataSource
import com.jero3000.appinstaller.repository.credential.datasource.CredentialPreferencesDataSource
import com.jero3000.appinstaller.repository.file.datasource.FileDataSource
import com.jero3000.appinstaller.repository.file.datasource.FtpFileDataSource
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.named
import org.koin.core.module.dsl.withOptions

import org.koin.dsl.module

actual val systemDataSourceModule = module{
    factoryOf(::FtpFileDataSource){ bind<FileDataSource>() } withOptions {
        named("ftp")
    }

    factory<Settings.Factory> { PreferencesSettings.Factory() }
}