package com.carspotter.data.dao.car_model

import com.carspotter.data.model.CarModel

interface ICarModelDAO {
    suspend fun getCarModelId(brand: String, model: String): Int?
    suspend fun getAllCarBrands(): List<String>
    suspend fun getCarModelsForBrand(brand: String): List<String>
    suspend fun createCarModel(carModel: CarModel): Int
    suspend fun getCarModel(carModelId: Int): CarModel?
    suspend fun getAllCarModels(): List<CarModel>
    suspend fun deleteCarModel(carModelId: Int): Int
}