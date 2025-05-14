package com.carspotter.data.model

import java.time.Instant

data class Post(
    val id: Int = 0,
    val userId: Int,
    val carModelId: Int,
    val imagePath: String,
    val description: String? = null,
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null,
)