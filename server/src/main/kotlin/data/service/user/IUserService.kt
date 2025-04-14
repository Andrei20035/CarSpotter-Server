package com.carspotter.data.service.user

import com.carspotter.data.model.User

interface IUserService {
    suspend fun createUser(user: User): Int
//    suspend fun login(username: String, password: String): UserDTO?
    suspend fun getUserByID(userId: Int): User?
    suspend fun getUserByUsername(username: String): User?
    suspend fun getAllUsers(): List<User>
    suspend fun updateProfilePicture(userId: Int, imagePath: String): Int
    suspend fun deleteUser(credentialId: Int): Int
}