package com.buenosaires.connect.features.experiences.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buenosaires.connect.core.data.MomentDao
import com.buenosaires.connect.core.location.LocationRepository
import com.buenosaires.connect.core.model.Moment
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class AddMomentViewModel @Inject constructor(
    private val momentDao: MomentDao,
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _location = MutableStateFlow(DEFAULT_LOCATION)
    val location: StateFlow<String> = _location

    fun fetchLocation() {
        viewModelScope.launch {
            locationRepository.getCurrentLocation().collect { (latitude, longitude) ->
                _location.value = buildString {
                    append("Buenos Aires ")
                    append('(')
                    append(latitude.formatCoordinate())
                    append(", ")
                    append(longitude.formatCoordinate())
                    append(')')
                }
            }
        }
    }

    fun addMoment(imageUri: Uri, description: String) {
        viewModelScope.launch {
            momentDao.insert(
                Moment(
                    imageUri = imageUri.toString(),
                    description = description.trim(),
                    date = System.currentTimeMillis(),
                    location = location.value
                )
            )
        }
    }

    private fun Double.formatCoordinate(): String = "%.4f".format(this)

    companion object {
        private const val DEFAULT_LOCATION = "Buenos Aires"
    }
}
