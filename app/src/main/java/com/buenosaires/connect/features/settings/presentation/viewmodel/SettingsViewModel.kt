package com.buenosaires.connect.features.settings.presentation.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.buenosaires.connect.core.data.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _selectedLanguage = MutableStateFlow(
        sharedPreferences.getString(LANGUAGE_KEY, DEFAULT_LANGUAGE) ?: DEFAULT_LANGUAGE
    )
    val selectedLanguage: StateFlow<String> = _selectedLanguage

    fun updateLanguage(languageCode: String) {
        _selectedLanguage.value = languageCode
        sharedPreferences.edit().putString(LANGUAGE_KEY, languageCode).apply()
    }

    fun logout(navController: NavController) {
        viewModelScope.launch {
            userRepository.logout()
            navController.navigate("registration") {
                popUpTo(navController.graph.id) {
                    inclusive = true
                }
            }
        }
    }

    companion object {
        private const val LANGUAGE_KEY = "language"
        private const val DEFAULT_LANGUAGE = "en"
    }
}
