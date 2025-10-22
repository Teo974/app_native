package com.buenosaires.connect.core.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.buenosaires.connect.core.model.User

@Dao
interface UserDao {
    @Insert
    suspend fun insert(user: User)

    @Query("SELECT * FROM users WHERE username = :username")
    suspend fun getUserByUsername(username: String): User?

    @Query("SELECT * FROM users LIMIT 1")
    suspend fun getAnyUser(): User?
}
