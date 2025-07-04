package com.carspotter.data.repository.comment

import com.carspotter.data.model.Comment
import java.util.*

interface ICommentRepository {
    suspend fun addComment(userId: UUID, postId: UUID, commentText: String): UUID
    suspend fun deleteComment(commentId: UUID): Int
    suspend fun getCommentsForPost(postId: UUID): List<Comment>
    suspend fun getCommentById(commentId: UUID): Comment?
}