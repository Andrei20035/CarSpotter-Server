package com.carspotter.data.dao.car_model

import com.carspotter.data.model.CarModel
import com.carspotter.data.table.CarModels
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertReturning
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class CarModelDaoImpl : ICarModelDAO {
    override suspend fun createCarModel(carModel: CarModel): Int {
        return transaction {
            CarModels
                .insertReturning(listOf(CarModels.id)) {
                    it[brand] = carModel.brand
                    it[model] = carModel.model
                    it[year] = carModel.year
                }.singleOrNull()?.get(CarModels.id) ?: error("Failed to insert car model")
        }
    }

    override suspend fun getCarModel(carModelId: Int): CarModel? {
        return transaction {
            CarModels
                .selectAll()
                .where { CarModels.id eq carModelId }
                .mapNotNull { row ->
                    CarModel(
                        id = row[CarModels.id],
                        brand = row[CarModels.brand],
                        model = row[CarModels.model],
                        year = row[CarModels.year]
                    )
                }.singleOrNull()
        }
    }

    override suspend fun getAllCarModels(): List<CarModel> {
        return transaction {
            CarModels
                .selectAll()
                .orderBy(CarModels.brand to SortOrder.ASC, CarModels.model to SortOrder.ASC, CarModels.year to SortOrder.DESC)
                .mapNotNull { row ->
                    CarModel(
                        id = row[CarModels.id],
                        brand = row[CarModels.brand],
                        model = row[CarModels.model],
                        year = row[CarModels.year]
                    )
                }
        }
    }

    override suspend fun deleteCarModel(carModelId: Int): Int {
        return transaction {
            CarModels
                .deleteWhere { id eq carModelId }
        }
    }


}