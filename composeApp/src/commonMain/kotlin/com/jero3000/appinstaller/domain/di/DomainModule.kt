package com.jero3000.appinstaller.domain.di

import com.jero3000.appinstaller.domain.CheckAdbServerRunningUseCase
import com.jero3000.appinstaller.domain.ClearCacheUseCase
import com.jero3000.appinstaller.domain.ClearCredentialsUseCase
import com.jero3000.appinstaller.domain.DiscoverDevicesUseCase
import com.jero3000.appinstaller.domain.EnsureAdbServerRunningUseCase
import com.jero3000.appinstaller.domain.GetAdbBinaryUseCase
import com.jero3000.appinstaller.domain.PutAdbBinaryUseCase
import com.jero3000.appinstaller.domain.GetAppConfigFlowUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import com.jero3000.appinstaller.domain.GetAppConfigUseCase
import com.jero3000.appinstaller.domain.GetPackageFileUseCase
import com.jero3000.appinstaller.domain.InstallAppPackageUseCase
import com.jero3000.appinstaller.domain.LoadAppConfigUseCase
import com.jero3000.appinstaller.domain.ResolvePackageUrlUseCase
import com.jero3000.appinstaller.domain.StoreCredentialsUseCase

val domainModule = module {
    factoryOf(::LoadAppConfigUseCase)
    factoryOf(::GetAppConfigFlowUseCase)
    factoryOf(::GetAppConfigUseCase)
    factoryOf(::GetPackageFileUseCase)
    factoryOf(::ResolvePackageUrlUseCase)
    factoryOf(::StoreCredentialsUseCase)
    factoryOf(::ClearCredentialsUseCase)
    factoryOf(::DiscoverDevicesUseCase)
    factoryOf(::InstallAppPackageUseCase)
    factoryOf(::ClearCacheUseCase)
    factoryOf(::GetAdbBinaryUseCase)
    factoryOf(::PutAdbBinaryUseCase)
    factoryOf(::EnsureAdbServerRunningUseCase)
    factoryOf(::CheckAdbServerRunningUseCase)
}
