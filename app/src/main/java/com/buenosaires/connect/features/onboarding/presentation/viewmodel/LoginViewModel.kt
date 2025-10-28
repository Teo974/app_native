package com.buenosaires.connect.features.onboarding.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buenosaires.connect.features.onboarding.data.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _loginCompleted = MutableStateFlow(false)
    val loginCompleted: StateFlow<Boolean> = _loginCompleted.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _logoutConfirmed = MutableStateFlow(false)
    val logoutConfirmed: StateFlow<Boolean> = _logoutConfirmed.asStateFlow()

    fun loginUser(username: String, password: String) {
        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            delay(1000) // Simulate network delay

            if (userRepository.isValidUser(username, password)) {
                _loginCompleted.value = true
            } else {
                _error.value = "Credenciales inválidas"
            }
            _isLoading.value = false
        }
    }

    fun acknowledgeCompletion() {
        _loginCompleted.value = false
    }

    fun logoutUser() {
        userRepository.logout()
        // Reset login state to allow re-login
        _loginCompleted.value = false
        _error.value = null
        _logoutConfirmed.value = true // Signal that logout is confirmed
    }

    fun acknowledgeLogoutConfirmation() {
        _logoutConfirmed.value = false
    }
}
