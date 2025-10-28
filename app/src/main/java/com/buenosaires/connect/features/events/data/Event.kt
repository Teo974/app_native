package com.buenosaires.connect.features.events.data

import androidx.annotation.DrawableRes
import org.osmdroid.util.GeoPoint

data class Event(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val imageUrl: String? = null, // Rendu nullable
    @DrawableRes val imageResId: Int? = null, // Nouvelle propriété pour l'ID de ressource drawable
    val category: String,
    val status: EventStatus,
    val location: GeoPoint,
    val address: String,
    val creatorId: String,
    val subscribers: List<String> = emptyList()
)
