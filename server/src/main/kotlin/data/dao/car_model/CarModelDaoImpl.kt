package com.carspotter.data.dao.car_model

import com.carspotter.data.model.CarModel
import com.carspotter.data.table.CarModels
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class CarModelDaoImpl : ICarModelDAO {
    override suspend fun createCarModel(carModel: CarModel): UUID {
        return transaction {
            CarModels
                .insertReturning(listOf(CarModels.id)) {
                    it[brand] = carModel.brand
                    it[model] = carModel.model
                    it[startYear] = carModel.startYear
                    it[endYear] = carModel.endYear
                }.singleOrNull()?.get(CarModels.id)?.value ?: throw IllegalStateException("Failed to insert car model")
        }
    }

    override suspend fun getCarModel(carModelId: UUID): CarModel? {
        return transaction {
            CarModels
                .selectAll()
                .where { CarModels.id eq carModelId }
                .mapNotNull { row ->
                    CarModel(
                        id = row[CarModels.id].value,
                        brand = row[CarModels.brand],
                        model = row[CarModels.model],
                        startYear = row[CarModels.startYear],
                        endYear = row[CarModels.endYear]
                    )
                }.singleOrNull()
        }
    }

    override suspend fun getCarModelId(brand: String, model: String): UUID? {
        return transaction {
            CarModels
                .selectAll()
                .where { (CarModels.brand eq brand) and (CarModels.model eq model) }
                .mapNotNull { it[CarModels.id].value }
                .singleOrNull()
        }
    }

    override suspend fun getAllCarBrands(): List<String> {
        return transaction {
            CarModels
                .select(CarModels.brand)
                .withDistinct()
                .orderBy(CarModels.brand to SortOrder.ASC)
                .map { it[CarModels.brand] }
        }
    }

    override suspend fun getCarModelsForBrand(brand: String): List<String> {
        return transaction {
            CarModels
                .select(CarModels.model)
                .orderBy(CarModels.model to SortOrder.ASC)
                .where { CarModels.brand eq brand }
                .withDistinct()
                .map { it[CarModels.model] }
        }
    }

    override suspend fun getAllCarModels(): List<CarModel> {
        return transaction {
            CarModels
                .selectAll()
                .orderBy(CarModels.brand to SortOrder.ASC, CarModels.model to SortOrder.ASC)
                .mapNotNull { row ->
                    CarModel(
                        id = row[CarModels.id].value,
                        brand = row[CarModels.brand],
                        model = row[CarModels.model],
                        startYear = row[CarModels.startYear],
                        endYear = row[CarModels.endYear]
                    )
                }
        }
    }

    override suspend fun deleteCarModel(carModelId: UUID): Int {
        return transaction {
            CarModels
                .deleteWhere { id eq carModelId }
        }
    }


}