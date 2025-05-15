package com.carspotter.data.dto

import com.carspotter.data.model.Comment
import java.time.Instant

data class CommentDTO(
    val id: Int,
    val userId: Int,
    val postId: Int,
    val commentText: String,
    val createdAt: Instant? = null,
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