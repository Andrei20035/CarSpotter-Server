package com.carspotter.data.table

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.timestamp

object Posts : Table("posts") {
    val id = integer("id").autoIncrement()
    val userId = integer("user_id").references(Users.id, onDelete = ReferenceOption.CASCADE)
    val carModelId = integer("car_model_id").references(CarModels.id, onDelete = ReferenceOption.NO_ACTION)
    val imagePath = text("image_path")
    val description = text("description").nullable()
    val timestamp = timestamp("timestamp").defaultExpression(CurrentTimestamp)

    override val primaryKey = PrimaryKey(id)
}
