package com.carspotter.data.service.user_car

import com.carspotter.data.model.User
import com.carspotter.data.model.UserCar

interface IUserCarService {
    suspend fun createUserCar(userCar: UserCar): Int
    suspend fun getUserCarById(userCarId: Int): UserCar?
    suspend fun getUserCarByUserId(userId: Int): UserCar?
    suspend fun getUserByUserCarId(userCarId: Int): User
    suspend fun updateUserCar(userId: Int, imagePath: String?, carModelId: Int?)
    suspend fun deleteUserCar(userId: Int)
    suspend fun getAllUserCars(): List<UserCar>
}