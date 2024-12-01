package com.jero3000.appinstaller.platform.localization

import java.util.Locale

class LocaleManagerImpl : LocaleManager {

    override fun getLocale(): AppLocale {
        val systemLanguage = Locale.getDefault().language
        println(systemLanguage)
        return AppLocale.entries.firstOrNull{ it.code == systemLanguage } ?: AppLocale.English
    }

    override fun setLocale(locale: AppLocale) {
        Locale.setDefault(Locale.of(locale.code))
    }
}