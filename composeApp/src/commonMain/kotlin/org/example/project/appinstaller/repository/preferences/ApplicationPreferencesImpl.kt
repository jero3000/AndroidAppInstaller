package org.example.project.appinstaller.repository.preferences

import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class ApplicationPreferencesImpl(context: String,
                                 settingsFactory: Settings.Factory,
                                 private val ioContext: CoroutineContext): ApplicationPreferences {

    private val settings =  settingsFactory.create(context)

    override suspend fun putString(key: String, data: String) = withContext(ioContext) {
        settings.putString(key, data)
    }

    override suspend fun getString(key: String) = withContext(ioContext) {
        settings.getStringOrNull(key)
    }

    override suspend fun putInt(key: String, data: Int) = withContext(ioContext) {
        settings.putInt(key, data)
    }

    override suspend fun getInt(key: String): Int? = withContext(ioContext) {
        settings.getIntOrNull(key)
    }

    override suspend fun remove(key: String) = withContext(ioContext){
        settings.remove(key)
    }

    override suspend fun clear() = withContext(ioContext){
        settings.clear()
    }
}
