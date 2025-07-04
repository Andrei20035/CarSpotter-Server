package com.carspotter.data.model

import java.time.Instant
import java.util.*

data class Post(
    val id: UUID = UUID.randomUUID(),
    val userId: UUID,
    val carModelId: UUID,
    val imagePath: String,
    val description: String? = null,
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null,
)