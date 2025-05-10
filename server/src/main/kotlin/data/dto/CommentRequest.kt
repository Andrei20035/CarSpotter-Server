package com.carspotter.data.dto

data class CommentRequest(
    val postId: Int,
    val commentText: String,
)
