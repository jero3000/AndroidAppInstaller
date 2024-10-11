package org.example.project.appinstaller.di

import org.example.project.appinstaller.filesystem.FileSystem
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import org.example.project.appinstaller.filesystem.FileSystemImpl
import org.koin.core.module.dsl.bind

actual val platformModule: Module = module {
    factoryOf(::FileSystemImpl) { bind<FileSystem>() }
}
