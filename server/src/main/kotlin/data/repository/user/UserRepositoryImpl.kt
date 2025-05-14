package com.carspotter.data.repository.user

import com.carspotter.data.dao.user.IUserDAO
import com.carspotter.data.model.User

class UserRepositoryImpl(
    private val userDao: IUserDAO
) : IUserRepository {
    override suspend fun createUser(user: User): Int {
        return userDao.createUser(user)
    }

    override suspend fun getUserByID(userId: Int): User? {
        return userDao.getUserByID(userId)
    }

    override suspend fun getUserByUsername(username: String): List<User> {
        return userDao.getUserByUsername(username)
    }

    override suspend fun getAllUsers(): List<User> {
        return userDao.getAllUsers()
    }

    override suspend fun updateProfilePicture(userId: Int, imagePath: String): Int {
        return userDao.updateProfilePicture(userId, imagePath)
    }

    override suspend fun deleteUser(credentialId: Int): Int {
        return userDao.deleteUser(credentialId)
    }
}