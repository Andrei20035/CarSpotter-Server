package com.carspotter.data.dao.user

import com.carspotter.data.model.User
import java.util.*

interface IUserDAO {
    suspend fun createUser(user: User): UUID
    suspend fun getUserByID(userId: UUID): User?
    suspend fun getUserByUsername(username: String): List<User>
    suspend fun getAllUsers(): List<User>
    suspend fun updateProfilePicture(userId: UUID, imagePath: String): Int
    suspend fun deleteUser(credentialId: UUID): Int
}