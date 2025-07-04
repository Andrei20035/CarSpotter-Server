package com.carspotter.data.dto

import com.carspotter.data.model.Post
import com.carspotter.serialization.InstantSerializer
import com.carspotter.serialization.UUIDSerializer
import kotlinx.serialization.Serializable
import java.time.Instant
import java.util.*

@Serializable
data class PostDTO(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID = UUID.randomUUID(),
    @Serializable(with = UUIDSerializer::class)
    val userId: UUID,
    @Serializable(with = UUIDSerializer::class)
    val carModelId: UUID,
    val imagePath: String,
    val description: String? = null,
    @Serializable(with = InstantSerializer::class)
    val createdAt: Instant? = null,
    @Serializable(with = InstantSerializer::class)
    val updatedAt: Instant? = null,
)

fun Post.toDTO() = PostDTO(
    id = this.id,
    userId = this.userId,
    carModelId = this.carModelId,
    imagePath = this.imagePath,
    description = this.description,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt
)