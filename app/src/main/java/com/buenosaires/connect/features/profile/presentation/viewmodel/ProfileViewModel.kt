package com.buenosaires.connect.features.profile.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.buenosaires.connect.features.onboarding.data.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    val username: StateFlow<String?> = userRepository.loggedInUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _description = MutableStateFlow("Viajero y entusiasta de la cultura porteña.")
    val description: StateFlow<String> = _description.asStateFlow()

    private val _profilePictureUri = MutableStateFlow<Uri?>(null)
    val profilePictureUri: StateFlow<Uri?> = _profilePictureUri.asStateFlow()

    private val _isSaveEnabled = MutableStateFlow(false)
    val isSaveEnabled: StateFlow<Boolean> = _isSaveEnabled.asStateFlow()

    private val originalDescription = _description.value
    private var originalProfilePictureUri = _profilePictureUri.value

    fun onDescriptionChange(newDescription: String) {
        _description.value = newDescription
        _isSaveEnabled.value = newDescription != originalDescription
    }

    fun onProfilePictureChange(newUri: Uri) {
        _profilePictureUri.value = newUri
        _isSaveEnabled.value = newUri != originalProfilePictureUri
    }

    fun onSaveClick() {
        _isSaveEnabled.value = false
    }

    fun onLogoutClick(navController: NavController) {
        viewModelScope.launch {
            userRepository.logout()
            navController.navigate("auth") {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
            }
        }
    }
}
