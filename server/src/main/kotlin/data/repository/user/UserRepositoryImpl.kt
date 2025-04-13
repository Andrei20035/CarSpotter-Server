package com.carspotter.data.repository.UserRepository

import com.carspotter.data.dao.user.UserDaoImpl
import com.carspotter.data.model.User
import com.carspotter.data.repository.user.IUserRepository

class UserRepositoryImpl(
    private val userDao: UserDaoImpl
) : IUserRepository {
    override suspend fun createUser(user: User): Int {
        return userDao.createUser(user)
    }

    override suspend fun getUserByID(userId: Int): User? {
        return userDao.getUserByID(userId)
    }

    override suspend fun getUserByUsername(username: String): User? {
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