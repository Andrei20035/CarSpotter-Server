package com.carspotter.data.service.user

import com.carspotter.data.dto.UserDTO
import com.carspotter.data.model.User
import java.util.*

interface IUserService {
    suspend fun createUser(user: User): UUID
    suspend fun getUserById(userId: UUID): UserDTO?
    suspend fun getUserByUsername(username: String): List<UserDTO>
    suspend fun getAllUsers(): List<UserDTO>
    suspend fun updateProfilePicture(userId: UUID, imagePath: String): Int
    suspend fun deleteUser(credentialId: UUID): Int
}