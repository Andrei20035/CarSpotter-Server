package com.carspotter.data.model

import java.time.Instant
import java.util.*

data class UserCar(
    val id: UUID = UUID.randomUUID(),
    val userId: UUID,
    val carModelId: UUID,
    val imagePath: String? = null,
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null,
)