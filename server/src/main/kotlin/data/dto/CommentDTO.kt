package com.carspotter.data.dto

import com.carspotter.data.model.Comment
import com.carspotter.serialization.InstantSerializer
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class CommentDTO(
    val id: Int,
    val userId: Int,
    val postId: Int,
    val commentText: String,
    @Serializable(with = InstantSerializer::class)
    val createdAt: Instant? = null,
    @Serializable(with = InstantSerializer::class)
    val updatedAt: Instant? = null,
)

fun Comment.toDTO() = CommentDTO(
    id = this.id,
    userId = this.id,
    postId = this.id,
    commentText = this.commentText,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
)