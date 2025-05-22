package com.carspotter.data.service.comment

import com.carspotter.data.dto.CommentDTO

interface ICommentService {
    suspend fun addComment(userId: Int, postId: Int, commentText: String): Int
    suspend fun deleteComment(commentId: Int): Int
    suspend fun getCommentsForPost(postId: Int): List<CommentDTO>
    suspend fun getCommentById(commentId: Int): CommentDTO?
}