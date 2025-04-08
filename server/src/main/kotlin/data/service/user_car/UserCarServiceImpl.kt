package com.carspotter.data.service.user_car

import com.carspotter.data.model.User
import com.carspotter.data.model.UserCar
import com.carspotter.data.repository.user_car.UserCarRepositoryImpl

class UserCarServiceImpl(
    private val userCarRepository: UserCarRepositoryImpl
): IUserCarService {
    override suspend fun createUserCar(userCar: UserCar): Int {
        return userCarRepository.createUserCar(userCar)
    }

    override suspend fun getUserCarById(userCarId: Int): UserCar? {
        return userCarRepository.getUserCarById(userCarId)
    }

    override suspend fun getUserCarByUserId(userId: Int): UserCar? {
        return userCarRepository.getUserCarByUserId(userId)
    }

    override suspend fun getUserByUserCarId(userCarId: Int): User {
        return userCarRepository.getUserByUserCarId(userCarId)
    }

    override suspend fun updateUserCar(userId: Int, imagePath: String?, carModelId: Int?) {
        userCarRepository.updateUserCar(userId, imagePath, carModelId)
    }

    override suspend fun deleteUserCar(userId: Int) {
        userCarRepository.deleteUserCar(userId)
    }

    override suspend fun getAllUserCars(): List<UserCar> {
        return userCarRepository.getAllUserCars()
    }
}