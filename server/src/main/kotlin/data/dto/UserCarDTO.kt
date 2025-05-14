package com.carspotter.data.dto

import com.carspotter.data.model.UserCar
import java.time.Instant

data class UserCarDTO(
    val id: Int = 0,
    val userId: Int,
    val carModelId: Int,
    val imagePath: String? = null,
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null
)

fun UserCar.toDTO() = UserCarDTO(
    id = this.id,
    userId = this.id,
    carModelId = this.id,
    imagePath = this.imagePath,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt
)
