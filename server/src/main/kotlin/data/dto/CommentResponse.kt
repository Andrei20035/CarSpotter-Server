package com.carspotter.data.dto

import com.carspotter.data.model.Comment

data class CommentResponse(
    val commentText: String
)

fun Comment.toResponse() = CommentResponse(
    commentText = this.commentText
)