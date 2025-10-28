package com.buenosaires.connect.features.onboarding.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buenosaires.connect.features.onboarding.data.UserRepository
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _registrationCompleted = MutableStateFlow(false)
    val registrationCompleted: StateFlow<Boolean> = _registrationCompleted.asStateFlow()

    private val _error = MutableStateFlow<RegistrationError?>(null)
    val error: StateFlow<RegistrationError?> = _error.asStateFlow()

    fun registerUser(username: String, email: String, password: String) {
        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            delay(1500) // Simulate network delay

            if (username.isBlank() || email.isBlank() || password.isBlank()) {
                _error.value = RegistrationError.REQUIRED_FIELDS
                _isLoading.value = false
                return@launch
            }

            val success = userRepository.addUser(username, password)
            if (success) {
                _registrationCompleted.value = true
            } else {
                _error.value = RegistrationError.USERNAME_TAKEN
            }
            _isLoading.value = false
        }
    }

    fun signUpWithGoogle(credential: AuthCredential) {
        _isLoading.value = true
        _error.value = null
        viewModelScope.launch {
            try {
                firebaseAuth.signInWithCredential(credential).await()
                _registrationCompleted.value = true
            } catch (e: Exception) {
                _error.value = RegistrationError.GOOGLE_SIGN_UP_FAILED
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun acknowledgeCompletion() {
        _registrationCompleted.value = false
    }

    enum class RegistrationError {
        REQUIRED_FIELDS,
        USERNAME_TAKEN,
        GOOGLE_SIGN_UP_FAILED
    }
}
