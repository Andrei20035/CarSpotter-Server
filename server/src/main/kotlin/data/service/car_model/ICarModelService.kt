package com.carspotter.data.service.car_model

import com.carspotter.data.model.CarModel

interface ICarModelService {
    suspend fun createCarModel(carModel: CarModel): Int
    suspend fun getCarModelById(carModelId: Int): CarModel?
    suspend fun getAllCarModels(): List<CarModel>
    suspend fun deleteCarModel(carModelId: Int): Int
}