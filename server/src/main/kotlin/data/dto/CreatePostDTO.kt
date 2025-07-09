package com.carspotter.data.dto

import java.util.UUID

data class CreatePostDTO(
    val userId: UUID,
    val carModelId: UUID,
    val imagePath: String,
    val latitude: Double,
    val longitude: Double,
    val description: String? = null
)

