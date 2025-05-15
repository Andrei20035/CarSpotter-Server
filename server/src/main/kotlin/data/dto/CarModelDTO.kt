package com.carspotter.data.dto

import com.carspotter.data.model.CarModel

data class CarModelDTO(
    val id: Int = 0,
    val brand: String,
    val model: String,
    val year: Int? = null,
)

fun CarModel.toDTO() = CarModelDTO(
    id = this.id,
    brand = this.brand,
    model = this.model,
    year = this.year,
)