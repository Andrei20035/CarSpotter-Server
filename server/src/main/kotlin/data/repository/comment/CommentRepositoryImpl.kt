package com.carspotter.data.repository.comment

import com.carspotter.data.dao.comment.ICommentDAO
import com.carspotter.data.model.Comment
import java.util.*

class CommentRepositoryImpl(
    private val commentDao: ICommentDAO,
) : ICommentRepository {
    override suspend fun addComment(userId: UUID, postId: UUID, commentText: String): UUID {
        return commentDao.addComment(userId, postId, commentText)
    }

    override suspend fun deleteComment(commentId: UUID): Int {
        return commentDao.deleteComment(commentId)
    }

    override suspend fun getCommentsForPost(postId: UUID): List<Comment> {
        return commentDao.getCommentsForPost(postId)
    }

    override suspend fun getCommentById(commentId: UUID): Comment? {
        return commentDao.getCommentById(commentId)
    }
}