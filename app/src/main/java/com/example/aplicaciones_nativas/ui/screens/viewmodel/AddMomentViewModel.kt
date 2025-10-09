package com.example.aplicaciones_nativas.ui.screens.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicaciones_nativas.data.LocationRepository
import com.example.aplicaciones_nativas.data.Moment
import com.example.aplicaciones_nativas.data.MomentDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddMomentViewModel @Inject constructor(
    private val momentDao: MomentDao,
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _location = MutableStateFlow("")
    val location = _location.asStateFlow()

    fun fetchLocation() {
        viewModelScope.launch {
            val (latitude, longitude) = locationRepository.getCurrentLocation().first()
            _location.value = "$latitude, $longitude"
        }
    }

    fun addMoment(imageUri: Uri, description: String) {
        viewModelScope.launch {
            val moment = Moment(
                imageUri = imageUri.toString(),
                description = description,
                date = System.currentTimeMillis(),
                location = _location.value
            )
            momentDao.insert(moment)
        }
    }
}
