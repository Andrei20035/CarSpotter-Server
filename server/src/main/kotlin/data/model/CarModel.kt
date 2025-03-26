package com.carspotter.data.model

data class CarModel(
    val id: Int = 0,
    val brand: String,
    val model: String,
    val year: Int? = null,
)