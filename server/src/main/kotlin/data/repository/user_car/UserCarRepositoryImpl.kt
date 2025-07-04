package com.carspotter.data.repository.user_car

import com.carspotter.data.dao.user_car.IUserCarDAO
import com.carspotter.data.model.User
import com.carspotter.data.model.UserCar
import java.util.*

class UserCarRepositoryImpl(
    private val userCarDao: IUserCarDAO,
) : IUserCarRepository {
    override suspend fun createUserCar(userCar: UserCar): UUID {
        return userCarDao.createUserCar(userCar)
    }

    override suspend fun getUserCarById(userCarId: UUID): UserCar? {
        return userCarDao.getUserCarById(userCarId)
    }

    override suspend fun getUserCarByUserId(userId: UUID): UserCar? {
        return userCarDao.getUserCarByUserId(userId)
    }

    override suspend fun getUserByUserCarId(userCarId: UUID): User {
        return userCarDao.getUserByUserCarId(userCarId)
    }

    override suspend fun updateUserCar(userId: UUID, imagePath: String?, carModelId: UUID?): Int {
        return userCarDao.updateUserCar(userId, imagePath, carModelId)
    }

    override suspend fun deleteUserCar(userId: UUID): Int {
        return userCarDao.deleteUserCar(userId)
    }

    override suspend fun getAllUserCars(): List<UserCar> {
        return userCarDao.getAllUserCars()
    }
}