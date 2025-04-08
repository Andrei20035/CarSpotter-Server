package com.carspotter.data.service.comment

import com.carspotter.data.model.Comment

interface ICommentService {
    suspend fun addComment(userId: Int, postId: Int, commentText: String): Int
    suspend fun removeComment(commentId: Int): Int
    suspend fun getCommentsForPost(postId: Int): List<Comment>
}