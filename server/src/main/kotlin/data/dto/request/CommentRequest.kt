package com.carspotter.data.dto.request

data class CommentRequest(
    val postId: Int,
    val commentText: String,
)
