package com.carspotter.data.repository.comment

import com.carspotter.data.model.Comment

interface ICommentRepository {
    suspend fun addComment(userId: Int, postId: Int, commentText: String): Int
    suspend fun removeComment(commentId: Int): Int
    suspend fun getCommentsForPost(postId: Int): List<Comment>
}