package org.example.project.appinstaller.platform.di

import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.example.project.appinstaller.platform.filesystem.PlatformFileSystem
import org.example.project.appinstaller.platform.filesystem.PlatformFileSystemImpl
import org.example.project.appinstaller.platform.uri.UriParser
import org.example.project.appinstaller.platform.uri.UriParserImpl
import org.koin.dsl.module

actual val platformSystemModule = module{
    factoryOf(::PlatformFileSystemImpl){ bind<PlatformFileSystem>() }
    factoryOf(::UriParserImpl){ bind<UriParser>() }
}
