package com.carspotter.data.table

import org.jetbrains.exposed.sql.Table

object CarModels : Table("car_models") {
    val id = integer("id").autoIncrement()
    val brand = varchar("brand", 50)
    val model = varchar("model", 50)
    val startYear = integer("start_year")
    val endYear = integer("end_year")

    override val primaryKey = PrimaryKey(id)

    init {
        uniqueIndex(brand, model)
    }

}
