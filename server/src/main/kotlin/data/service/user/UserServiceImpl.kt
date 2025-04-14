package com.carspotter.data.service.user

import com.carspotter.data.model.User
import com.carspotter.data.repository.user.UserRepositoryImpl

class UserServiceImpl(
    private val userRepository: UserRepositoryImpl
): IUserService {
    override suspend fun createUser(user: User): Int {
        return userRepository.createUser(user)
    }

//    override suspend fun login(username: String, password: String): UserDTO? {
//        val user = userRepository.getUserByUsername(username) ?: return null
//        val result = BCrypt.verifyer().verify(password.toCharArray(), user.)
//    }

    override suspend fun getUserByID(userId: Int): User? {
        return userRepository.getUserByID(userId)
    }

    override suspend fun getUserByUsername(username: String): User? {
        return userRepository.getUserByUsername(username)
    }

    override suspend fun getAllUsers(): List<User> {
        return userRepository.getAllUsers()
    }

    override suspend fun updateProfilePicture(userId: Int, imagePath: String): Int {
        return userRepository.updateProfilePicture(userId, imagePath)
    }

    override suspend fun deleteUser(credentialId: Int): Int {
        return userRepository.deleteUser(credentialId)
    }
}