package com.jero3000.appinstaller.platform.localization

interface LocaleManager {
    fun getLocale(): AppLocale
    fun setLocale(locale: AppLocale)
}