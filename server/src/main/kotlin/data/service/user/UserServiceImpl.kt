package com.carspotter.data.service.user

import at.favre.lib.crypto.bcrypt.BCrypt
import com.carspotter.data.dto.UserDTO
import com.carspotter.data.model.User
import com.carspotter.data.repository.UserRepository.UserRepositoryImpl

class UserServiceImpl(
    private val userRepository: UserRepositoryImpl
): IUserService {
    override suspend fun createUser(user: User): Int {
        val hashedPassword = BCrypt.withDefaults().hashToString(12, user.password.toCharArray())
        val userToSave = user.copy(password = hashedPassword)
        return userRepository.createUser(userToSave)
    }

    override suspend fun login(username: String, password: String): UserDTO? {
        val user = userRepository.getUserByUsername(username) ?: return null
        val result = BCrypt.verifyer().verify(password.toCharArray(), user.)
    }

    override suspend fun getUserByID(userId: Int): UserDTO? {
        return userRepository.getUserByID(userId)
    }

    override suspend fun getUserByUsername(username: String): UserDTO? {
        return userRepository.getUserByUsername(username)
    }

    override suspend fun getAllUsers(): List<UserDTO> {
        return userRepository.getAllUsers()
    }

    override suspend fun updateProfilePicture(userId: Int, imagePath: String) {
        userRepository.updateProfilePicture(userId, imagePath)
    }

    override suspend fun deleteUser(userId: Int) {
        userRepository.deleteUser(userId)
    }
}