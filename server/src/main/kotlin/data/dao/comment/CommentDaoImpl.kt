package com.carspotter.data.dao.comment

import com.carspotter.data.model.Comment
import com.carspotter.data.table.Comments
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertReturning
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class CommentDaoImpl : ICommentDAO {
    override suspend fun addComment(userId: UUID, postId: UUID, commentText: String): UUID {
        return transaction {
            Comments
                .insertReturning(listOf(Comments.id)) {
                    it[Comments.userId] = userId
                    it[Comments.postId] = postId
                    it[Comments.commentText] = commentText
                }.singleOrNull()?.get(Comments.id)?.value ?: throw IllegalStateException("Failed to insert comment")
        }
    }

    override suspend fun deleteComment(commentId: UUID): Int {
        return transaction {
            Comments
                .deleteWhere { id eq commentId }
        }
    }

    override suspend fun getCommentsForPost(postId: UUID): List<Comment> {
        return transaction {
            Comments
                .selectAll()
                .where { Comments.postId eq postId }
                .mapNotNull { row ->
                    Comment(
                        id = row[Comments.id].value,
                        userId = row[Comments.userId],
                        postId = row[Comments.postId],
                        commentText = row[Comments.commentText],
                        createdAt = row[Comments.createdAt],
                        updatedAt = row[Comments.updatedAt],
                    )
                }
        }
    }

    override suspend fun getCommentById(commentId: UUID): Comment? {
        return transaction {
            Comments
                .selectAll()
                .where { Comments.id eq commentId }
                .mapNotNull { row ->
                    Comment(
                        id = row[Comments.id].value,
                        userId = row[Comments.userId],
                        postId = row[Comments.postId],
                        commentText = row[Comments.commentText],
                    )
                }.singleOrNull()
        }
    }

}