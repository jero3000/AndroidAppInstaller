package com.jero3000.appinstaller.repository.preferences

interface ApplicationPreferences {
    suspend fun putString(key: String, data: String)
    suspend fun getString(key: String): String?
    suspend fun putInt(key: String, data: Int)
    suspend fun getInt(key: String): Int?
    suspend fun putBoolean(key: String, data: Boolean)
    suspend fun getBoolean(key: String): Boolean?
    suspend fun remove(key:String)
    suspend fun clear()
}