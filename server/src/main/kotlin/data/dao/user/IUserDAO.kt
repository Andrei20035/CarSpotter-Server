package com.carspotter.data.dao.user

import com.carspotter.data.model.User

interface IUserDAO {
    suspend fun createUser(user: User): Int
    suspend fun getUserByID(userId: Int): User?
    suspend fun getUserByUsername(username: String): User?
    suspend fun getAllUsers(): List<User>
    suspend fun updateProfilePicture(userId: Int, imagePath: String): Int
    suspend fun deleteUser(credentialId: Int): Int
}