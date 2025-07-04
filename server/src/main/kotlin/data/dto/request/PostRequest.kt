package com.carspotter.data.dto.request

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
)

fun PostRequest.toPost(userId: UUID) = Post(
    userId = userId,
    carModelId = this.carModelId,
    imagePath = this.imagePath,
    description = this.description,
)
