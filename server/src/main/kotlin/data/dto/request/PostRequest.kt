package com.carspotter.data.dto.request

import com.carspotter.data.dto.CreatePostDTO
import com.carspotter.data.model.Post
import com.carspotter.serialization.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class PostRequest(
    @Serializable(with = UUIDSerializer::class)
    val carModelId: UUID,
    val imagePath: String,
    val description: String? = null,
    val latitude: Double,
    val longitude: Double,
)

fun PostRequest.addId(userId: UUID) = CreatePostDTO(
    userId = userId,
    carModelId = this.carModelId,
    imagePath = this.imagePath,
    description = this.description,
    latitude = this.latitude,
    longitude = this.longitude,
)
