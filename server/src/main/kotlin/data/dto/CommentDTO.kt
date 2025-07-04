package com.carspotter.data.dto

import com.carspotter.data.model.Comment
import com.carspotter.serialization.InstantSerializer
import com.carspotter.serialization.UUIDSerializer
import kotlinx.serialization.Serializable
import java.time.Instant
import java.util.*

@Serializable
data class CommentDTO(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    @Serializable(with = UUIDSerializer::class)
    val userId: UUID,
    @Serializable(with = UUIDSerializer::class)
    val postId: UUID,
    val commentText: String,
    @Serializable(with = InstantSerializer::class)
    val createdAt: Instant? = null,
    @Serializable(with = InstantSerializer::class)
    val updatedAt: Instant? = null,
)

fun Comment.toDTO() = CommentDTO(
    id = this.id,
    userId = this.userId,
    postId = this.postId,
    commentText = this.commentText,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
)