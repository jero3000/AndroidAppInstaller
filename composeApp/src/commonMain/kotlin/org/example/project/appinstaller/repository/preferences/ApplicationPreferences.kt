package org.example.project.appinstaller.repository.preferences

interface ApplicationPreferences {
    suspend fun putString(key: String, data: String)
    suspend fun getString(key: String): String?
    suspend fun putInt(key: String, data: Int)
    suspend fun getInt(key: String): Int?
    suspend fun remove(key:String)
    suspend fun clear()
}