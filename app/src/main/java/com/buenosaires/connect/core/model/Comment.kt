package com.buenosaires.connect.core.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "comments")
data class Comment(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val momentId: Long,
    val author: String,
    val content: String,
    val timestamp: Long
)
