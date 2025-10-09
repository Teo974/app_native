package com.example.aplicaciones_nativas.ui.screens.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicaciones_nativas.data.User
import com.example.aplicaciones_nativas.data.UserDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.security.MessageDigest
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(private val userDao: UserDao) : ViewModel() {

    fun registerUser(username: String, email: String, password: String) {
        viewModelScope.launch {
            val passwordHash = hashPassword(password)
            val user = User(username = username, email = email, passwordHash = passwordHash)
            userDao.insert(user)
        }
    }

    private fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }
}
