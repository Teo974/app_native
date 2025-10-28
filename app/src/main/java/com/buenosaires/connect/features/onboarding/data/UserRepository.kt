package com.buenosaires.connect.features.onboarding.data

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor() {
    private val users = mutableMapOf<String, String>()

    private val _loggedInUser = MutableSharedFlow<String?>(replay = 1)
    val loggedInUser: SharedFlow<String?> = _loggedInUser

    init {
        // Add a default user for testing
        users["test"] = "test"
        _loggedInUser.tryEmit(null) // Initialize with no user logged in
    }

    fun addUser(username: String, password: String): Boolean {
        if (users.containsKey(username)) {
            return false // User already exists
        }
        users[username] = password
        return true
    }

    fun isValidUser(username: String, password: String): Boolean {
        val isValid = users[username] == password
        if (isValid) {
            _loggedInUser.tryEmit(username)
        }
        return isValid
    }

    fun logout() {
        _loggedInUser.tryEmit(null)
    }
}