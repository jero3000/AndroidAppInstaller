package com.jero3000.appinstaller.repository.config

import dev.zwander.kotlin.file.IPlatformFile
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import com.jero3000.appinstaller.model.AppConfig
import com.jero3000.appinstaller.repository.config.datasource.ConfigurationDataSource

class ConfigurationRepositoryImpl(private val dataSource: ConfigurationDataSource) :
    ConfigurationRepository {
    private val loadChannel = Channel<IPlatformFile>()
    private var appConfig: AppConfig? = null

    override fun getConfiguration(): AppConfig? {
        return appConfig
    }

    override fun getConfigurationFlow() = flow {
        dataSource.getConfiguration().getOrNull()?.let {
            appConfig = it
            emit(Result.success(it))
        }
        while(true){
            val result = dataSource.loadConfiguration(loadChannel.receive())
            result.getOrNull()?.let {
                appConfig = it
            }
            emit(result)
        }
    }

    override fun loadConfiguration(file: IPlatformFile) {
        loadChannel.trySend(file)
    }
}