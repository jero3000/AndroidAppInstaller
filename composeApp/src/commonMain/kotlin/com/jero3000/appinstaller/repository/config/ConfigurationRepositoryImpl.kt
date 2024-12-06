package com.jero3000.appinstaller.repository.config

import com.jero3000.appinstaller.model.AppConfig
import com.jero3000.appinstaller.repository.config.datasource.ConfigurationDataSource
import dev.zwander.kotlin.file.IPlatformFile
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.flow

class ConfigurationRepositoryImpl(private val dataSource: ConfigurationDataSource) :
    ConfigurationRepository {
    private val loadChannel = Channel<IPlatformFile>()
    private var appConfig: AppConfig? = null

    override suspend fun getConfiguration(): AppConfig? {
        return appConfig ?: run {
            dataSource.getConfiguration().getOrNull()?.also {
                appConfig = it
            }
        }
    }

    override fun getConfigurationFlow() = flow {
        getConfiguration()?.let { emit(Result.success(it)) }
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

    override suspend fun clearConfiguration() {
        dataSource.clearConfiguration()
    }
}