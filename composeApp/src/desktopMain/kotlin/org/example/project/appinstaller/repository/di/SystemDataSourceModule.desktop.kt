package org.example.project.appinstaller.repository.di

import org.example.project.appinstaller.repository.file.datasource.FileDataSource
import org.example.project.appinstaller.repository.file.datasource.FtpFileDataSource
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.named
import org.koin.core.module.dsl.withOptions

import org.koin.dsl.module

actual val systemDataSourceModule = module{
    factoryOf(::FtpFileDataSource){ bind<FileDataSource>() } withOptions {
        named("ftp")
    }
}