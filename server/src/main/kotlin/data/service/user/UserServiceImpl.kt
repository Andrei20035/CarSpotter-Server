package com.carspotter.data.service.user

import com.carspotter.data.dao.user.UserCreationException
import com.carspotter.data.dto.UserDTO
import com.carspotter.data.dto.toDTO
import com.carspotter.data.model.User
import com.carspotter.data.repository.user.IUserRepository
import java.util.*

class UserServiceImpl(
    private val userRepository: IUserRepository
): IUserService {
    override suspend fun createUser(user: User): UUID {
        return try {
            userRepository.createUser(user)
        } catch (e: UserCreationException) {
            if (e.message?.contains("username") == true) {
                throw UsernameAlreadyExistsException("Username is already taken")
            }
            throw e
        }
    }

    override suspend fun getUserById(userId: UUID): UserDTO? {
        return userRepository.getUserByID(userId)?.toDTO()
    }

    override suspend fun getUserByUsername(username: String): List<UserDTO> {
        return userRepository.getUserByUsername(username).map { it.toDTO() }
    }

    override suspend fun getAllUsers(): List<UserDTO> {
        return userRepository.getAllUsers().map { it.toDTO() }
    }

    override suspend fun updateProfilePicture(userId: UUID, imagePath: String): Int {
        return userRepository.updateProfilePicture(userId, imagePath)
    }

    override suspend fun deleteUser(credentialId: UUID): Int {
        return userRepository.deleteUser(credentialId)
    }
}

class UsernameAlreadyExistsException(message: String) : Exception(message)

