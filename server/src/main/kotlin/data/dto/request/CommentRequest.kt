package com.carspotter.data.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class CommentRequest(
    val postId: Int,
    val commentText: String,
)
