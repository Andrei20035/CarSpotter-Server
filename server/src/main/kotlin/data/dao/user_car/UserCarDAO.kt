package com.carspotter.data.dao.user_car

import com.carspotter.data.model.User
import com.carspotter.data.model.UserCar

interface UserCarDAO {
    suspend fun createUserCar(userCar: UserCar): Int
    suspend fun getUserCarById(userCarId: Int): UserCar?
    suspend fun getUserCarByUserId(userId: Int): UserCar?
    suspend fun getUserByUserCarId(userCarId: Int): User
    suspend fun updateUserCar(userId: Int, imagePath: String?, carModelId: Int?): Int
    suspend fun deleteUserCar(userId: Int): Int
    suspend fun getAllUserCars(): List<UserCar>
}