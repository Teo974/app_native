package com.buenosaires.connect.features.events.data

import org.osmdroid.util.GeoPoint

object MockEventRepository {

    // Mock admin user ID
    const val ADMIN_USER_ID = "admin123"

    // Mock events - Emptied for user to create
    val mockEvents = mutableListOf<Event>() // Changed to mutable list

    fun getEvents(): List<Event> = mockEvents

    fun getEventById(id: String): Event? = mockEvents.find { it.id == id }

    fun addEvent(event: Event) {
        mockEvents.add(event)
    }
}
