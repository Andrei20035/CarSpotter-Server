package com.carspotter.data.repository.UserRepository

import com.carspotter.data.dao.user.UserDaoImpl
import com.carspotter.data.dto.UserDTO
import com.carspotter.data.model.User

class UserRepositoryImpl(
    private val userDao: UserDaoImpl
) : IUserRepository {
    override suspend fun createUser(user: User): Int {
        return userDao.createUser(user)
    }

    override suspend fun getUserByID(userId: Int): UserDTO? {
        return userDao.getUserByID(userId)
    }

    override suspend fun getUserByUsername(username: String): UserDTO? {
        return userDao.getUserByUsername(username)
    }

    override suspend fun getAllUsers(): List<UserDTO> {
        return userDao.getAllUsers()
    }

    override suspend fun updateProfilePicture(userId: Int, imagePath: String) {
        userDao.updateProfilePicture(userId, imagePath)
    }

    override suspend fun deleteUser(userId: Int) {
        userDao.deleteUser(userId)
    }
}