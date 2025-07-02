package com.carspotter.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CarModel(
    val id: Int = 0,
    val brand: String,
    val model: String,
    val startYear: Int,
    val endYear: Int,
)