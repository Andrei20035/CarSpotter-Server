package com.carspotter.data.dto

import com.carspotter.data.model.Post
import com.carspotter.serialization.InstantSerializer
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class PostDTO(
    val id: Int = 0,
    val userId: Int,
    val carModelId: Int,
    val imagePath: String,
    val description: String? = null,
    @Serializable(with = InstantSerializer::class)
    val createdAt: Instant? = null,
    @Serializable(with = InstantSerializer::class)
    val updatedAt: Instant? = null,
)

fun Post.toDTO() = PostDTO(
    id = this.id,
    userId = this.id,
    carModelId = this.id,
    imagePath = this.imagePath,
    description = this.description,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt
)