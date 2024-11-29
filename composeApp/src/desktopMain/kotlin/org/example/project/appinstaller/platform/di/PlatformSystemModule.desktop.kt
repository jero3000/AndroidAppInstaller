package org.example.project.appinstaller.platform.di

import kotlinx.coroutines.Dispatchers
import org.example.project.appinstaller.platform.adb.AdbBinary
import org.example.project.appinstaller.platform.adb.AdbBinaryImpl
import org.example.project.appinstaller.platform.device.DeviceManager
import org.example.project.appinstaller.platform.device.DadbDeviceManager
import org.example.project.appinstaller.platform.device.JadbDeviceManager
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.example.project.appinstaller.platform.filesystem.PlatformFileSystem
import org.example.project.appinstaller.platform.filesystem.PlatformFileSystemImpl
import org.example.project.appinstaller.platform.intent.BrowserLauncher
import org.example.project.appinstaller.platform.intent.BrowserLauncherImpl
import org.example.project.appinstaller.platform.localization.LocaleManager
import org.example.project.appinstaller.platform.localization.LocaleManagerImpl
import org.example.project.appinstaller.platform.uri.UriParser
import org.example.project.appinstaller.platform.uri.UriParserImpl
import org.koin.dsl.module

actual val platformSystemModule = module{
    factoryOf(::PlatformFileSystemImpl){ bind<PlatformFileSystem>() }
    factoryOf(::UriParserImpl){ bind<UriParser>() }
    single<DeviceManager> { JadbDeviceManager(Dispatchers.IO) }
    factory<AdbBinary> { AdbBinaryImpl(Dispatchers.IO) }
    factoryOf(::BrowserLauncherImpl){ bind<BrowserLauncher>() }
    single<LocaleManager> { LocaleManagerImpl() }
}
