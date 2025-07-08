package com.carspotter.data.model

import java.time.Instant
import java.util.*

data class Post(
    val id: UUID = UUID.randomUUID(),
    val userId: UUID,
    val carModelId: UUID,
    val imagePath: String,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val description: String? = null,
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null,
)