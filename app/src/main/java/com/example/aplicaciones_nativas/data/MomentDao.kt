package com.example.aplicaciones_nativas.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MomentDao {
    @Insert
    suspend fun insert(moment: Moment)

    @Update
    suspend fun update(moment: Moment)

    @Delete
    suspend fun delete(moment: Moment)

    @Query("SELECT * FROM moments ORDER BY date DESC")
    fun getAllMoments(): Flow<List<Moment>>

    @Query("SELECT * FROM moments WHERE id = :momentId")
    fun getMomentById(momentId: Long): Flow<Moment>

    @Query("SELECT * FROM moments WHERE description LIKE :tag ORDER BY date DESC")
    fun getMomentsWithTag(tag: String): Flow<List<Moment>>
}
