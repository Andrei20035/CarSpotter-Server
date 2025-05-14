package com.carspotter.data.dto.response

import com.carspotter.data.model.Comment

data class CommentResponse(
    val commentText: String
)

fun Comment.toResponse() = CommentResponse(
    commentText = this.commentText
)