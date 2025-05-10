package com.carspotter.data.repository.comment

import com.carspotter.data.dao.comment.ICommentDAO
import com.carspotter.data.model.Comment

class CommentRepositoryImpl(
    private val commentDao: ICommentDAO,
) : ICommentRepository {
    override suspend fun addComment(userId: Int, postId: Int, commentText: String): Int {
        return commentDao.addComment(userId, postId, commentText)
    }

    override suspend fun deleteComment(commentId: Int): Int {
        return commentDao.deleteComment(commentId)
    }

    override suspend fun getCommentsForPost(postId: Int): List<Comment> {
        return commentDao.getCommentsForPost(postId)
    }

    override suspend fun getCommentById(commentId: Int): Comment? {
        return commentDao.getCommentById(commentId)
    }
}