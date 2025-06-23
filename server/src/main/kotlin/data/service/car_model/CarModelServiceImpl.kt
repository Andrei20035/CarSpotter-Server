package com.carspotter.data.service.car_model

import com.carspotter.data.model.CarModel
import com.carspotter.data.repository.car_model.ICarModelRepository

class CarModelServiceImpl(
    private val carModelRepository: ICarModelRepository
): ICarModelService {
    override suspend fun getCarModelId(brand: String, model: String): Int? {
        return carModelRepository.getCarModelId(brand, model)
    }

    override suspend fun getAllCarBrands(): List<String> {
        return carModelRepository.getAllCarBrands()
    }

    override suspend fun getCarModelsForBrand(brand: String): List<String> {
        return carModelRepository.getCarModelsForBrand(brand)
    }

    override suspend fun createCarModel(carModel: CarModel): Int {
        return try {
            carModelRepository.createCarModel(carModel)
        } catch (e: IllegalStateException) {
            throw IllegalArgumentException( "Failed to add car model: ${carModel.brand} ${carModel.model} (${carModel.year})", e)
        }
    }

    override suspend fun getCarModelById(carModelId: Int): CarModel? {
        return carModelRepository.getCarModel(carModelId)
    }

    override suspend fun getAllCarModels(): List<CarModel> {
        return carModelRepository.getAllCarModels()
    }

    override suspend fun deleteCarModel(carModelId: Int): Int {
        return carModelRepository.deleteCarModel(carModelId)
    }

}