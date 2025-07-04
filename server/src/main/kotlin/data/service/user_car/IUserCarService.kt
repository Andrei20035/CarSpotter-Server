package com.carspotter.data.service.user_car

import com.carspotter.data.dto.UserCarDTO
import com.carspotter.data.dto.UserDTO
import com.carspotter.data.model.UserCar
import java.util.*

interface IUserCarService {
    suspend fun createUserCar(userCar: UserCar): UUID
    suspend fun getUserCarById(userCarId: UUID): UserCarDTO?
    suspend fun getUserCarByUserId(userId: UUID): UserCarDTO?
    suspend fun getUserByUserCarId(userCarId: UUID): UserDTO
    suspend fun updateUserCar(userId: UUID, imagePath: String?, carModelId: UUID?): Int
    suspend fun deleteUserCar(userId: UUID): Int
    suspend fun getAllUserCars(): List<UserCarDTO>
}