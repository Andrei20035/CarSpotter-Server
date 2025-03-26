package com.carspotter.data.model

data class UserCar (
    val id: Int = 0,
    val userId: Int,
    val carModelId: Int,
    val imagePath: String? = null
)