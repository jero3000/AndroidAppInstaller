package org.example.project.appinstaller.repository.preferences

interface ApplicationPreferences {
    suspend fun putString(key: String, data:String)
    suspend fun getString(key: String): String?
    suspend fun remove(key:String)
    suspend fun clear()
}