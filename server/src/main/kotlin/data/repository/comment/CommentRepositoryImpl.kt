package com.carspotter.data.repository.comment

import com.carspotter.data.dao.comment.CommentDaoImpl
import com.carspotter.data.model.Comment

class CommentRepositoryImpl(
    private val commentDao: CommentDaoImpl,
): ICommentRepository {
    override suspend fun addComment(userId: Int, postId: Int, commentText: String): Int {
        return commentDao.addComment(userId, postId, commentText)
    }

    override suspend fun removeComment(commentId: Int): Int {
        return commentDao.removeComment(commentId)
    }

    override suspend fun getCommentsForPost(postId: Int): List<Comment> {
        return commentDao.getCommentsForPost(postId)
    }
}