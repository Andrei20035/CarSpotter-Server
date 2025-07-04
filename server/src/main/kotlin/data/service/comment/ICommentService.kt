package com.carspotter.data.service.comment

import com.carspotter.data.dto.CommentDTO
import java.util.*

interface ICommentService {
    suspend fun addComment(userId: UUID, postId: UUID, commentText: String): UUID
    suspend fun deleteComment(commentId: UUID): Int
    suspend fun getCommentsForPost(postId: UUID): List<CommentDTO>
    suspend fun getCommentById(commentId: UUID): CommentDTO?
}