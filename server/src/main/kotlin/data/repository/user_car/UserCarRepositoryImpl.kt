package com.carspotter.data.repository.user_car

import com.carspotter.data.dao.user_car.UserCarDaoImpl
import com.carspotter.data.model.User
import com.carspotter.data.model.UserCar

class UserCarRepositoryImpl(
    private val userCarDao: UserCarDaoImpl,
) : IUserCarRepository {
    override suspend fun createUserCar(userCar: UserCar): Int {
        return userCarDao.createUserCar(userCar)
    }

    override suspend fun getUserCarById(userCarId: Int): UserCar? {
        return userCarDao.getUserCarById(userCarId)
    }

    override suspend fun getUserCarByUserId(userId: Int): UserCar? {
        return userCarDao.getUserCarByUserId(userId)
    }

    override suspend fun getUserByUserCarId(userCarId: Int): User {
        return userCarDao.getUserByUserCarId(userCarId)
    }

    override suspend fun updateUserCar(userId: Int, imagePath: String?, carModelId: Int?) {
        userCarDao.updateUserCar(userId, imagePath, carModelId)
    }

    override suspend fun deleteUserCar(userId: Int) {
        userCarDao.deleteUserCar(userId)
    }

    override suspend fun getAllUserCars(): List<UserCar> {
        return userCarDao.getAllUserCars()
    }
}