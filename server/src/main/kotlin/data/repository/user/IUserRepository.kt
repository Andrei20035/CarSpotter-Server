package com.carspotter.data.repository.UserRepository

import com.carspotter.data.dto.UserDTO
import com.carspotter.data.model.User

interface IUserRepository {
    suspend fun createUser(user: User): Int
    suspend fun getUserByID(userId: Int): UserDTO?
    suspend fun getUserByUsername(username: String): UserDTO?
    suspend fun getAllUsers(): List<UserDTO>
    suspend fun updateProfilePicture(userId: Int, imagePath: String)
    suspend fun deleteUser(userId: Int)
}