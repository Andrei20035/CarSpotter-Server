package com.carspotter.data.dao.comment

import com.carspotter.data.model.Comment
import com.carspotter.data.table.Comments
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteReturning
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertReturning
import org.jetbrains.exposed.sql.transactions.transaction

class CommentDaoImpl: CommentDAO {
    override suspend fun addComment(userId: Int, postId: Int, commentText: String): Int {
        return transaction {
            Comments
                .insertReturning(listOf(Comments.id)) {
                    it[Comments.userId] = userId
                    it[Comments.postId] = postId
                    it[Comments.commentText] = commentText
                }.singleOrNull()?.get(Comments.id) ?: error("Failed to insert comment")
        }
    }

    override suspend fun removeComment(commentId: Int): Int {
        return transaction {
            Comments
                .deleteWhere { id eq commentId }
        }
    }

    override suspend fun getCommentsForPost(postId: Int): List<Comment> {
        return transaction {
            Comments
                .select(Comments.postId eq postId)
                .mapNotNull { row ->
                    Comment(
                        id = row[Comments.id],
                        userId = row[Comments.userId],
                        postId = row[Comments.postId],
                        commentText = row[Comments.commentText],
                        timestamp = row[Comments.timestamp]
                    )
                }
        }
    }

}