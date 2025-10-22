package com.buenosaires.connect.features.experiences.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buenosaires.connect.core.data.MomentDao
import com.buenosaires.connect.core.model.Moment
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class EditMomentViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val momentDao: MomentDao
) : ViewModel() {

    private val momentId: Long = checkNotNull(savedStateHandle[MOMENT_ID_ARG])

    val moment: StateFlow<Moment?> = momentDao.getMomentById(momentId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving

    fun updateMoment(description: String, location: String) {
        val current = moment.value ?: return
        viewModelScope.launch {
            _isSaving.value = true
            momentDao.update(
                current.copy(
                    description = description.trim(),
                    location = location
                )
            )
            _isSaving.value = false
        }
    }

    fun updateImage(newImageUri: Uri) {
        val current = moment.value ?: return
        viewModelScope.launch {
            momentDao.update(current.copy(imageUri = newImageUri.toString()))
        }
    }

    companion object {
        const val MOMENT_ID_ARG = "momentId"
    }
}
