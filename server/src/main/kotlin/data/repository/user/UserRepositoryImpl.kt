package com.carspotter.data.repository.user

import com.carspotter.data.dao.user.IUserDAO
import com.carspotter.data.model.User
import java.util.*

class UserRepositoryImpl(
    private val userDao: IUserDAO
) : IUserRepository {
    override suspend fun createUser(user: User): UUID {
        return userDao.createUser(user)
    }

    override suspend fun getUserByID(userId: UUID): User? {
        return userDao.getUserByID(userId)
    }

    override suspend fun getUserByUsername(username: String): List<User> {
        return userDao.getUserByUsername(username)
    }

    override suspend fun getAllUsers(): List<User> {
        return userDao.getAllUsers()
    }

    override suspend fun updateProfilePicture(userId: UUID, imagePath: String): Int {
        return userDao.updateProfilePicture(userId, imagePath)
    }

    override suspend fun deleteUser(credentialId: UUID): Int {
        return userDao.deleteUser(credentialId)
    }
}