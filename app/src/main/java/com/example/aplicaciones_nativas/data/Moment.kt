package com.example.aplicaciones_nativas.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "moments")
data class Moment(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val imageUri: String,
    val description: String,
    val date: Long,
    val location: String
)
