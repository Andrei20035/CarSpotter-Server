package com.carspotter.data.table

import org.jetbrains.exposed.dao.id.UUIDTable

object CarModels : UUIDTable("car_models") {
    val brand = varchar("brand", 50)
    val model = varchar("model", 50)
    val startYear = integer("start_year")
    val endYear = integer("end_year")

    init {
        uniqueIndex(brand, model)
    }

}
