package com.carspotter.data.service.user_car

import com.carspotter.data.dto.UserCarDTO
import com.carspotter.data.dto.UserDTO
import com.carspotter.data.dto.toDTO
import com.carspotter.data.model.UserCar
import com.carspotter.data.repository.user_car.IUserCarRepository

class UserCarServiceImpl(
    private val userCarRepository: IUserCarRepository
): IUserCarService {
    override suspend fun createUserCar(userCar: UserCar): Int {
        return try {
            userCarRepository.createUserCar(userCar)
        } catch (e: IllegalStateException) {
            throw UserCarCreationException("Invalid userId or carModelId", e)
        }
    }

    override suspend fun getUserCarById(userCarId: Int): UserCarDTO? {
        return userCarRepository.getUserCarById(userCarId)?.toDTO()
    }

    override suspend fun getUserCarByUserId(userId: Int): UserCarDTO? {
        return userCarRepository.getUserCarByUserId(userId)?.toDTO()
    }

    override suspend fun getUserByUserCarId(userCarId: Int): UserDTO {
        return userCarRepository.getUserByUserCarId(userCarId).toDTO()
    }

    override suspend fun updateUserCar(userId: Int, imagePath: String?, carModelId: Int?): Int {
        return userCarRepository.updateUserCar(userId, imagePath, carModelId)
    }

    override suspend fun deleteUserCar(userId: Int): Int {
        return userCarRepository.deleteUserCar(userId)
    }

    override suspend fun getAllUserCars(): List<UserCarDTO> {
        return userCarRepository.getAllUserCars().map { it.toDTO() }
    }
}

class UserCarCreationException(message: String, cause: Throwable? = null): Exception(message, cause)