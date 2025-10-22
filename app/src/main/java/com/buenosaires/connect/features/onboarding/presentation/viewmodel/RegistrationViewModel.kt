package com.buenosaires.connect.features.onboarding.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buenosaires.connect.core.data.UserDao
import com.buenosaires.connect.core.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.security.MessageDigest

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val userDao: UserDao
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<RegistrationError?>(null)
    val error: StateFlow<RegistrationError?> = _error

    private val _registrationCompleted = MutableStateFlow(false)
    val registrationCompleted: StateFlow<Boolean> = _registrationCompleted

    fun registerUser(username: String, email: String, password: String) {
        viewModelScope.launch {
            _error.value = null
            if (username.isBlank() || email.isBlank() || password.isBlank()) {
                _error.value = RegistrationError.REQUIRED_FIELDS
                return@launch
            }

            _isLoading.value = true
            val existing = userDao.getUserByUsername(username)
            if (existing != null) {
                _error.value = RegistrationError.USERNAME_TAKEN
                _isLoading.value = false
                return@launch
            }

            val user = User(
                username = username.trim(),
                email = email.trim(),
                passwordHash = password.sha256()
            )
            userDao.insert(user)
            _isLoading.value = false
            _registrationCompleted.value = true
        }
    }

    fun acknowledgeCompletion() {
        _registrationCompleted.value = false
    }

    enum class RegistrationError {
        REQUIRED_FIELDS,
        USERNAME_TAKEN
    }

    private fun String.sha256(): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(trim().toByteArray())
        return bytes.joinToString(separator = "") { byte -> "%02x".format(byte) }
    }
}
