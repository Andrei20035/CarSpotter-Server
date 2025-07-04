package com.carspotter.data.dao.user_car

import com.carspotter.data.model.User
import com.carspotter.data.model.UserCar
import java.util.*

interface IUserCarDAO {
    suspend fun createUserCar(userCar: UserCar): UUID
    suspend fun getUserCarById(userCarId: UUID): UserCar?
    suspend fun getUserCarByUserId(userId: UUID): UserCar?
    suspend fun getUserByUserCarId(userCarId: UUID): User
    suspend fun updateUserCar(userId: UUID, imagePath: String?, carModelId: UUID?): Int
    suspend fun deleteUserCar(userId: UUID): Int
    suspend fun getAllUserCars(): List<UserCar>
}