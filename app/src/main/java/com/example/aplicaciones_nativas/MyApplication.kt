package com.example.aplicaciones_nativas

import android.app.Application
import android.content.SharedPreferences
import android.content.res.Configuration
import dagger.hilt.android.HiltAndroidApp
import java.util.Locale
import javax.inject.Inject

@HiltAndroidApp
class MyApplication : Application() {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        val languageCode = sharedPreferences.getString("language", "en") ?: "en"
        val locale = Locale.forLanguageTag(languageCode)
        Locale.setDefault(locale)

        val configuration = resources.configuration
        configuration.setLocale(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }
}
