package org.example.project.appinstaller.platform.di

import org.example.project.appinstaller.platform.filesystem.FileSystem
import org.example.project.appinstaller.platform.filesystem.FileSystemImpl
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val platformModule = module{
    includes(platformSystemModule)

    factoryOf(::FileSystemImpl) { bind<FileSystem>() }
}

expect val platformSystemModule: Module