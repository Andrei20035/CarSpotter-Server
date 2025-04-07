package com.carspotter.data.repository.CarModelRepository

import com.carspotter.data.dao.car_model.CarModelDaoImpl
import com.carspotter.data.model.CarModel

class CarModelRepositoryImpl(
    private val carModelDao: CarModelDaoImpl
): ICarModelRepository {
    override suspend fun createCarModel(carModel: CarModel): Int {
        return carModelDao.createCarModel(carModel)
    }

    override suspend fun getCarModel(carModelId: Int): CarModel? {
        return carModelDao.getCarModel(carModelId)
    }

    override suspend fun getAllCarModels(): List<CarModel> {
        return carModelDao.getAllCarModels()
    }

    override suspend fun deleteCarModel(carModelId: Int) {
        carModelDao.deleteCarModel(carModelId)
    }
}