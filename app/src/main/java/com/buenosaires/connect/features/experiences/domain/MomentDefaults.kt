package com.buenosaires.connect.features.experiences.domain

import com.buenosaires.connect.core.model.Moment

object MomentDefaults {
    private const val ONE_DAY_MILLIS = 24 * 60 * 60 * 1000L

    val communityPosts: List<Moment> = listOf(
        Moment(
            id = Long.MIN_VALUE,
            imageUri = "https://unpeudargentine.com/wp-content/uploads/2024/05/317-1024x1024.jpg",
            description = "Tigre Sunset 2025 - \"Vibras del Caminito\" - Las casas pintadas se encienden con el amanecer porteño.",
            date = System.currentTimeMillis() - 2 * ONE_DAY_MILLIS,
            location = "La Boca, Buenos Aires"
        ),
        Moment(
            id = Long.MIN_VALUE + 1,
            imageUri = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR6uXb04pOpg_QaBS9HHl0pJnk0QNrECy66EQ&s",
            description = "Recoleta Jazz Nocturno - \"Calma entre jacarandás\" - Paseo matinal entre calles violetas y cafés clásicos.",
            date = System.currentTimeMillis() - ONE_DAY_MILLIS / 2,
            location = "Recoleta, Buenos Aires"
        )
    )

    fun findById(id: Long): Moment? = communityPosts.find { it.id == id }
}
