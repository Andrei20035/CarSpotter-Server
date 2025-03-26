package com.carspotter.data.table

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object UsersCars: Table("users_cars") {
    val id = integer("id").autoIncrement()
    val userId = integer("user_id").references(Users.id, onDelete = ReferenceOption.CASCADE).uniqueIndex()
    val carModelId = integer("car_model_id").references(CarModels.id, onDelete = ReferenceOption.CASCADE)
    val imagePath = text("image_path").nullable()

    override val primaryKey = PrimaryKey(id)

}