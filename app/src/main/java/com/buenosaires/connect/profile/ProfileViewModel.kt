package com.buenosaires.connect.profile

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

open class ProfileViewModel : ViewModel() {

    private val _description = MutableStateFlow("")
    open val description: StateFlow<String> = _description.asStateFlow()

    private val _profilePictureUri = MutableStateFlow<String?>(null) // URI for the profile picture
    open val profilePictureUri: StateFlow<String?> = _profilePictureUri.asStateFlow()

    private val _isSaveEnabled = MutableStateFlow(false)
    open val isSaveEnabled: StateFlow<Boolean> = _isSaveEnabled.asStateFlow()

    init {
        // Load initial profile data (e.g., from a repository)
        loadProfileData()
    }

    open fun onDescriptionChange(newDescription: String) {
        if (newDescription != _description.value) {
            _description.value = newDescription
            _isSaveEnabled.value = true
        }
    }

    open fun onProfilePictureClick() {
        // Handle opening image picker or camera
        // For now, let's simulate a change for demonstration
        _profilePictureUri.value = "some_new_uri" // Simulate a new URI
        _isSaveEnabled.value = true
    }

    open fun onSaveClick() {
        // Save the profile data (description, profilePictureUri) to a repository
        // After saving, disable the save button
        _isSaveEnabled.value = false
        // Show a toast or other feedback to the user
    }

    private fun loadProfileData() {
        // Simulate loading existing data
        _description.value = "Hello, I am a new user!"
        _profilePictureUri.value = null // No picture initially
    }
}
