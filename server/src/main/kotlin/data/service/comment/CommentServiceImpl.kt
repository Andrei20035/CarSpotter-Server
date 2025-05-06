package com.carspotter.data.service.comment

import com.carspotter.data.model.Comment
import com.carspotter.data.repository.comment.CommentRepositoryImpl
import com.carspotter.data.repository.comment.ICommentRepository

class CommentServiceImpl(
    private val commentRepository: ICommentRepository,
): ICommentService {
    override suspend fun addComment(userId: Int, postId: Int, commentText: String): Int {
        return commentRepository.addComment(userId, postId, commentText)
    }

    override suspend fun removeComment(commentId: Int): Int {
        return commentRepository.removeComment(commentId)
    }

    override suspend fun getCommentsForPost(postId: Int): List<Comment> {
        return commentRepository.getCommentsForPost(postId)
    }
}