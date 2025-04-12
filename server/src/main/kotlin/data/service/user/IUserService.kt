package com.carspotter.data.service.user

import com.carspotter.data.dto.UserDTO
import com.carspotter.data.model.User

interface IUserService {
    suspend fun createUser(user: User): Int
    suspend fun login(username: String, password: String): UserDTO?
    suspend fun getUserByID(userId: Int): UserDTO?
    suspend fun getUserByUsername(username: String): UserDTO?
    suspend fun getAllUsers(): List<UserDTO>
    suspend fun updateProfilePicture(userId: Int, imagePath: String)
    suspend fun deleteUser(userId: Int)
}