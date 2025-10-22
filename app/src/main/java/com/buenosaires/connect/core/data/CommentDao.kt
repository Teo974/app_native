package com.buenosaires.connect.core.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.buenosaires.connect.core.model.Comment
import kotlinx.coroutines.flow.Flow

@Dao
interface CommentDao {
    @Insert
    suspend fun insert(comment: Comment)

    @Query("SELECT * FROM comments WHERE momentId = :momentId ORDER BY timestamp DESC")
    fun getCommentsForMoment(momentId: Long): Flow<List<Comment>>

    @Query("SELECT * FROM comments ORDER BY timestamp DESC")
    fun getAllComments(): Flow<List<Comment>>

    @Query("DELETE FROM comments WHERE id = :id")
    suspend fun deleteById(id: Long)
}
