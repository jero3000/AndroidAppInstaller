package com.jero3000.appinstaller.platform.di

import kotlinx.coroutines.Dispatchers
import com.jero3000.appinstaller.platform.adb.AdbBinary
import com.jero3000.appinstaller.platform.adb.AdbBinaryImpl
import com.jero3000.appinstaller.platform.device.DeviceManager
import com.jero3000.appinstaller.platform.device.DadbDeviceManager
import com.jero3000.appinstaller.platform.device.JadbDeviceManager
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import com.jero3000.appinstaller.platform.filesystem.PlatformFileSystem
import com.jero3000.appinstaller.platform.filesystem.PlatformFileSystemImpl
import com.jero3000.appinstaller.platform.intent.BrowserLauncher
import com.jero3000.appinstaller.platform.intent.BrowserLauncherImpl
import com.jero3000.appinstaller.platform.localization.LocaleManager
import com.jero3000.appinstaller.platform.localization.LocaleManagerImpl
import com.jero3000.appinstaller.platform.uri.UriParser
import com.jero3000.appinstaller.platform.uri.UriParserImpl
import org.koin.dsl.module

actual val platformSystemModule = module{
    factoryOf(::PlatformFileSystemImpl){ bind<PlatformFileSystem>() }
    factoryOf(::UriParserImpl){ bind<UriParser>() }
    single<DeviceManager> { JadbDeviceManager(Dispatchers.IO) }
    factory<AdbBinary> { AdbBinaryImpl(Dispatchers.IO) }
    factoryOf(::BrowserLauncherImpl){ bind<BrowserLauncher>() }
    single<LocaleManager> { LocaleManagerImpl() }
}
