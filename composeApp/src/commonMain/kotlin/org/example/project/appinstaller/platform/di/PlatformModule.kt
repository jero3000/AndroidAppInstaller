package org.example.project.appinstaller.platform.di

import org.example.project.appinstaller.platform.filesystem.FileSystem
import org.example.project.appinstaller.platform.filesystem.FileSystemImpl
import org.example.project.appinstaller.platform.filesystem.FileUtils
import org.example.project.appinstaller.platform.filesystem.FileUtilsImpl
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val platformModule = module{
    includes(platformSystemModule)

    factoryOf(::FileUtilsImpl) { bind<FileUtils>() }
    factoryOf(::FileSystemImpl) { bind<FileSystem>() }
}

expect val platformSystemModule: Module