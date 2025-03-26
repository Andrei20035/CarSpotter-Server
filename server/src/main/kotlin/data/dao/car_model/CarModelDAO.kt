package com.carspotter.data.dao.car_model

import com.carspotter.data.model.CarModel

interface CarModelDAO {
    suspend fun createCarModel(carModel: CarModel): Int
    suspend fun getCarModel(carModelId: Int): CarModel?
    suspend fun getAllCarModels(): List<CarModel>
    suspend fun deleteCarModel(carModelId: Int)
}