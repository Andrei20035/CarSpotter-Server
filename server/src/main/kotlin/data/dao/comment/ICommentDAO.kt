package com.carspotter.data.dao.comment

import com.carspotter.data.model.Comment
import java.util.*

interface ICommentDAO {
    suspend fun addComment(userId: UUID, postId: UUID, commentText: String): UUID
    suspend fun deleteComment(commentId: UUID): Int
    suspend fun getCommentsForPost(postId: UUID): List<Comment>
    suspend fun getCommentById(commentId: UUID): Comment?
}