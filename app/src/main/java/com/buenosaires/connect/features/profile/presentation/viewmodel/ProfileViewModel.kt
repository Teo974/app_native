package com.buenosaires.connect.features.profile.presentation.viewmodel

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buenosaires.connect.core.data.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description

    private val _profilePictureUri = MutableStateFlow<Uri?>(null)
    val profilePictureUri: StateFlow<Uri?> = _profilePictureUri

    private val _isSaveEnabled = MutableStateFlow(false)
    val isSaveEnabled: StateFlow<Boolean> = _isSaveEnabled

    private var initialDescription: String? = ""
    private var initialProfilePictureUri: Uri? = null


    init {
        viewModelScope.launch {
            userRepository.getLoggedInUser()?.let { user ->
                _username.value = user.username
                _description.value = user.description ?: ""
                initialDescription = user.description
                user.profilePictureUri?.let {
                    _profilePictureUri.value = it.toUri()
                    initialProfilePictureUri = it.toUri()
                }
            }

            // Combine the flows to check for changes
            combine(_description, _profilePictureUri) { currentDescription, currentUri ->
                currentDescription != initialDescription || currentUri != initialProfilePictureUri
            }.collect {
                _isSaveEnabled.value = it
            }
        }
    }

    fun onDescriptionChange(newDescription: String) {
        _description.value = newDescription
    }

    fun onProfilePictureChange(newUri: Uri) {
        _profilePictureUri.value = newUri
    }

    fun onSaveClick() {
        viewModelScope.launch {
            val currentUser = userRepository.getLoggedInUser()
            currentUser?.let {
                val updatedUser = it.copy(
                    description = _description.value,
                    profilePictureUri = _profilePictureUri.value?.toString()
                )
                userRepository.insertUser(updatedUser)
                // After saving, update the initial state to the new state
                initialDescription = updatedUser.description
                initialProfilePictureUri = updatedUser.profilePictureUri?.toUri()
                _isSaveEnabled.value = false
            }
        }
    }
}
