package com.example.aplicaciones_nativas.ui.screens.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicaciones_nativas.data.Moment
import com.example.aplicaciones_nativas.data.MomentDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditMomentViewModel @Inject constructor(
    private val momentDao: MomentDao,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val momentId: Long = checkNotNull(savedStateHandle["momentId"])

    private val _moment = MutableStateFlow<Moment?>(null)
    val moment = _moment.asStateFlow()

    init {
        viewModelScope.launch {
            _moment.value = momentDao.getMomentById(momentId).first()
        }
    }

    fun updateMomentDescription(description: String) {
        viewModelScope.launch {
            val currentMoment = _moment.value
            if (currentMoment != null) {
                val updatedMoment = currentMoment.copy(description = description)
                momentDao.update(updatedMoment)
            }
        }
    }
}
