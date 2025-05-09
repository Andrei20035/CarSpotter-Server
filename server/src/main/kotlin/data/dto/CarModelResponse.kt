package com.carspotter.data.dto

import com.carspotter.data.model.CarModel

data class CarModelResponse(
    val brand: String,
    val model: String,
    val year: Int? = null,
)

fun CarModel.toResponse() = CarModelResponse(
    brand = this.brand,
    model = this.model,
    year = this.year,
)
