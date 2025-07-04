package com.carspotter.data.repository.car_model

import com.carspotter.data.dao.car_model.ICarModelDAO
import com.carspotter.data.model.CarModel
import java.util.*

class CarModelRepositoryImpl(
    private val carModelDao: ICarModelDAO
) : ICarModelRepository {
    override suspend fun getCarModelId(brand: String, model: String): UUID? {
        return carModelDao.getCarModelId(brand, model)
    }

    override suspend fun getAllCarBrands(): List<String> {
        return carModelDao.getAllCarBrands()
    }

    override suspend fun getCarModelsForBrand(brand: String): List<String> {
        return carModelDao.getCarModelsForBrand(brand)
    }

    override suspend fun createCarModel(carModel: CarModel): UUID {
        return carModelDao.createCarModel(carModel)
    }

    override suspend fun getCarModel(carModelId: UUID): CarModel? {
        return carModelDao.getCarModel(carModelId)
    }

    override suspend fun getAllCarModels(): List<CarModel> {
        return carModelDao.getAllCarModels()
    }

    override suspend fun deleteCarModel(carModelId: UUID): Int {
        return carModelDao.deleteCarModel(carModelId)
    }
}