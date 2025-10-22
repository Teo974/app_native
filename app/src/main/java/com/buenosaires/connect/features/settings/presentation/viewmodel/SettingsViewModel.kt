package com.buenosaires.connect.features.settings.presentation.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private val _selectedLanguage = MutableStateFlow(
        sharedPreferences.getString(LANGUAGE_KEY, DEFAULT_LANGUAGE) ?: DEFAULT_LANGUAGE
    )
    val selectedLanguage: StateFlow<String> = _selectedLanguage

    fun updateLanguage(languageCode: String) {
        _selectedLanguage.value = languageCode
        sharedPreferences.edit().putString(LANGUAGE_KEY, languageCode).apply()
    }

    companion object {
        private const val LANGUAGE_KEY = "language"
        private const val DEFAULT_LANGUAGE = "en"
    }
}
