package com.carspotter.data.service.car_model

import com.carspotter.data.model.CarModel
import java.util.*

interface ICarModelService {
    suspend fun getCarModelId(brand: String, model: String): UUID?
    suspend fun getAllCarBrands(): List<String>
    suspend fun getCarModelsForBrand(brand: String): List<String>
    suspend fun createCarModel(carModel: CarModel): UUID
    suspend fun getCarModelById(carModelId: UUID): CarModel?
    suspend fun getAllCarModels(): List<CarModel>
    suspend fun deleteCarModel(carModelId: UUID): Int
}