package com.buenosaires.connect.core.data

import com.buenosaires.connect.core.model.User
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(private val userDao: UserDao) {

    suspend fun getLoggedInUser(): User? {
        // In a real app, you would have logic to determine the currently logged-in user,
        // possibly from a session manager or DataStore.
        // For now, we'll return any user from the database as a placeholder.
        return userDao.getAnyUser()
    }

    suspend fun getUserByUsername(username: String): User? {
        return userDao.getUserByUsername(username)
    }

    suspend fun insertUser(user: User) {
        userDao.insert(user)
    }

    suspend fun logout() {
        userDao.deleteAll()
    }
}
