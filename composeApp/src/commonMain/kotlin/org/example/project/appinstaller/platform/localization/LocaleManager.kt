package org.example.project.appinstaller.platform.localization

interface LocaleManager {
    fun getLocale(): AppLocale
    fun setLocale(locale: AppLocale)
}