package com.jero3000.appinstaller.repository.di

import com.jero3000.appinstaller.repository.file.datasource.AwsS3FileDataSource
import com.jero3000.appinstaller.repository.file.datasource.FileDataSource
import com.jero3000.appinstaller.repository.file.datasource.FtpFileDataSource
import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.Settings
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.named
import org.koin.core.module.dsl.withOptions
import org.koin.dsl.module

actual val systemDataSourceModule = module{
    factoryOf(::FtpFileDataSource){ bind<FileDataSource>() } withOptions {
        named("ftp")
    }

    factoryOf(::AwsS3FileDataSource){ bind<FileDataSource>() } withOptions {
        named("s3")
    }

    factory<Settings.Factory> { PreferencesSettings.Factory() }
}