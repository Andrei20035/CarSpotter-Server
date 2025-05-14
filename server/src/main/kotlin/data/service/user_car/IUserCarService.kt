package com.carspotter.data.service.user_car

import com.carspotter.data.dto.UserCarDTO
import com.carspotter.data.dto.UserDTO
import com.carspotter.data.model.UserCar

interface IUserCarService {
    suspend fun createUserCar(userCar: UserCar): Int
    suspend fun getUserCarById(userCarId: Int): UserCarDTO?
    suspend fun getUserCarByUserId(userId: Int): UserCarDTO?
    suspend fun getUserByUserCarId(userCarId: Int): UserDTO
    suspend fun updateUserCar(userId: Int, imagePath: String?, carModelId: Int?): Int
    suspend fun deleteUserCar(userId: Int): Int
    suspend fun getAllUserCars(): List<UserCarDTO>
}