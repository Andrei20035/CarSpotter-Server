package com.carspotter.data.dto

import com.carspotter.data.model.CarModel

data class CarModelDTO(
    val id: Int = 0,
    val brand: String,
    val model: String,
    val startYear: Int,
    val endYear: Int,
)

fun CarModel.toDTO() = CarModelDTO(
    id = this.id,
    brand = this.brand,
    model = this.model,
    startYear = this.startYear,
    endYear = this.endYear
)