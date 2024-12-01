package com.jero3000.appinstaller.platform.di

import com.jero3000.appinstaller.platform.filesystem.FileSystem
import com.jero3000.appinstaller.platform.filesystem.FileSystemImpl
import com.jero3000.appinstaller.platform.filesystem.FileUtils
import com.jero3000.appinstaller.platform.filesystem.FileUtilsImpl
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