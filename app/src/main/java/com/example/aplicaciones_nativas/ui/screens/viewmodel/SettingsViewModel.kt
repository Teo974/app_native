package com.example.aplicaciones_nativas.ui.screens.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(private val sharedPreferences: SharedPreferences) : ViewModel() {

    fun saveLanguage(language: String) {
        sharedPreferences.edit().putString("language", language).apply()
    }

    fun getLanguage(): String {
        return sharedPreferences.getString("language", "en") ?: "en"
    }
}
