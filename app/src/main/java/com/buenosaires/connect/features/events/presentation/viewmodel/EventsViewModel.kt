package com.buenosaires.connect.features.events.presentation.viewmodel

import android.app.Application
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buenosaires.connect.R // Import R for drawable resources
import com.buenosaires.connect.features.events.data.Event
import com.buenosaires.connect.features.events.data.EventStatus
import com.buenosaires.connect.features.events.data.MockEventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import org.osmdroid.util.GeoPoint
import java.io.IOException
import java.util.Locale

@HiltViewModel
class EventsViewModel @Inject constructor(
    private val application: Application // Inject Application context
) : ViewModel() {

    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events.asStateFlow()

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    private val _selectedStatus = MutableStateFlow<EventStatus?>(null)
    val selectedStatus: StateFlow<EventStatus?> = _selectedStatus.asStateFlow()

    init {
        loadEvents()
    }

    private fun loadEvents() {
        viewModelScope.launch {
            // Add default Moscu Club event if not already present
            if (MockEventRepository.getEventById("moscu_club_id") == null) {
                val moscuClubAddress = "Av. Costanera Rafael Obligado 6151, C1428 Cdad. Autónoma de Buenos Aires"
                val moscuClubLocation = geocodeAddress(moscuClubAddress)
                val moscuClubEvent = Event(
                    id = "moscu_club_id",
                    title = "Moscu Club",
                    description = "Une soirée inoubliable avec les meilleurs DJs de musique électronique.",
                    imageUrl = null, // Set to null as we are using imageResId
                    imageResId = R.drawable.ic_moscu_club, // Use local drawable resource
                    category = "Música",
                    status = EventStatus.UPCOMING,
                    location = moscuClubLocation,
                    address = moscuClubAddress,
                    creatorId = MockEventRepository.ADMIN_USER_ID
                )
                MockEventRepository.addEvent(moscuClubEvent)
            }
            // Add default Niceto Club event if not already present
            if (MockEventRepository.getEventById("niceto_club_id") == null) {
                val nicetoClubAddress = "Cnel. Niceto Vega 5510, C1414BFD Cdad. Autónoma de Buenos Aires"
                val nicetoClubLocation = geocodeAddress(nicetoClubAddress)
                val nicetoClubEvent = Event(
                    id = "niceto_club_id",
                    title = "Niceto Club",
                    description = "Niceto Club est une discothèque et une salle de concert située à Buenos Aires, en Argentine. C'est l'un des lieux les plus emblématiques de la vie nocturne de la ville, connu pour sa programmation variée et son atmosphère vibrante.",
                    imageUrl = null, // Set to null as we are using imageResId
                    imageResId = R.drawable.ic_niceto_club, // Use local drawable resource
                    category = "Música",
                    status = EventStatus.UPCOMING,
                    location = nicetoClubLocation,
                    address = nicetoClubAddress,
                    creatorId = MockEventRepository.ADMIN_USER_ID
                )
                MockEventRepository.addEvent(nicetoClubEvent)
            }
            _events.value = MockEventRepository.getEvents()
            filterEvents()
        }
    }

    fun onSearchTextChange(text: String) {
        _searchText.value = text
        filterEvents()
    }

    fun onCategorySelected(category: String?) {
        _selectedCategory.value = category
        filterEvents()
    }

    fun onStatusSelected(status: EventStatus?) {
        _selectedStatus.value = status
        filterEvents()
    }

    private fun filterEvents() {
        val currentEvents = MockEventRepository.getEvents()
        val filtered = currentEvents.filter {
            (it.title.contains(_searchText.value, ignoreCase = true) ||
                it.description.contains(_searchText.value, ignoreCase = true)) &&
                (_selectedCategory.value == null || it.category == _selectedCategory.value) &&
                (_selectedStatus.value == null || it.status == _selectedStatus.value)
        }
        _events.value = filtered
    }

    fun getEventById(eventId: String): Event? {
        return MockEventRepository.getEventById(eventId)
    }

    fun isAdmin(userId: String): Boolean {
        // For now, a simple check against our mock admin ID
        return userId == MockEventRepository.ADMIN_USER_ID
    }

    fun subscribeToEvent(eventId: String, userId: String) {
        // TODO: Implement subscription logic (add userId to event's subscribers list)
        // This would typically involve updating the event in a database/backend
        val currentEvents = _events.value.toMutableList()
        val eventIndex = currentEvents.indexOfFirst { it.id == eventId }
        if (eventIndex != -1) {
            val updatedEvent = currentEvents[eventIndex].copy(
                subscribers = currentEvents[eventIndex].subscribers + userId
            )
            currentEvents[eventIndex] = updatedEvent
            _events.value = currentEvents
        }
    }

    // New function to add an event
    fun addEvent(
        title: String,
        description: String,
        imageUri: Uri?,
        imageResId: Int? = null, // Added imageResId parameter
        category: String,
        status: EventStatus,
        address: String,
        creatorId: String
    ) {
        viewModelScope.launch {
            val imageUrl = imageUri?.toString() // Convert Uri to String, now nullable

            val location = geocodeAddress(address)

            val newEvent = Event(
                title = title,
                description = description,
                imageUrl = imageUrl, // Pass imageUrl to the Event constructor
                imageResId = imageResId, // Pass imageResId to the Event constructor
                category = category,
                status = status,
                location = location,
                address = address, // Pass address to the Event constructor
                creatorId = creatorId
            )
            // Add the new event to the mock repository
            MockEventRepository.addEvent(newEvent)
            // Reload and filter events to update the UI
            filterEvents()
        }
    }

    private suspend fun geocodeAddress(address: String): GeoPoint {
        return withContext(Dispatchers.IO) {
            val geocoder = Geocoder(application, Locale.getDefault())
            var location = GeoPoint(0.0, 0.0) // Default location in case of geocoding failure
            try {
                Log.d("EventsViewModel", "Attempting to geocode address: $address")
                val addresses: List<Address>? = geocoder.getFromLocationName(address, 1)
                if (!addresses.isNullOrEmpty()) {
                    val fetchedAddress = addresses[0]
                    location = GeoPoint(fetchedAddress.latitude, fetchedAddress.longitude)
                    Log.d("EventsViewModel", "Geocoding successful. Lat: ${fetchedAddress.latitude}, Lon: ${fetchedAddress.longitude}")
                } else {
                    Log.d("EventsViewModel", "Geocoding returned no results for address: $address")
                }
            } catch (e: IOException) {
                Log.e("EventsViewModel", "Geocoding failed: ${e.message}")
                e.printStackTrace()
            }
            location
        }
    }
}
