package com.carspotter.data.service.comment

import com.carspotter.data.dto.CommentDTO
import com.carspotter.data.dto.toDTO
import com.carspotter.data.repository.comment.ICommentRepository
import java.util.*

class CommentServiceImpl(
    private val commentRepository: ICommentRepository,
): ICommentService {
    override suspend fun addComment(userId: UUID, postId: UUID, commentText: String): UUID {
        return try {
            commentRepository.addComment(userId, postId, commentText)
        } catch (e: IllegalStateException) {
            throw IllegalArgumentException("Failed to add comment: $commentText", e)
        }
    }

    override suspend fun deleteComment(commentId: UUID): Int {
        return commentRepository.deleteComment(commentId)
    }

    override suspend fun getCommentsForPost(postId: UUID): List<CommentDTO> {
        return commentRepository.getCommentsForPost(postId).map { it.toDTO() }
    }

    override suspend fun getCommentById(commentId: UUID): CommentDTO? {
        return commentRepository.getCommentById(commentId)?.toDTO()
    }
}