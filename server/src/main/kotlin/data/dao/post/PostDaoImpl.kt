package com.carspotter.data.dao.post

import com.carspotter.data.model.Post
import com.carspotter.data.table.Posts
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.util.*

class PostDaoImpl : IPostDAO {
    override suspend fun createPost(post: Post): UUID {
        return transaction {
            Posts.insertReturning(listOf(Posts.id)) {
                it[userId] = post.userId
                it[carModelId] = post.carModelId
                it[imagePath] = post.imagePath
                it[description] = post.description
            }.singleOrNull()?.get(Posts.id)?.value ?: throw IllegalStateException("Failed to insert post")
        }
    }

    override suspend fun getPostById(postId: UUID): Post? {
        return transaction {
            Posts
                .selectAll()
                .where { Posts.id eq postId }
                .mapNotNull { row ->
                    Post(
                        id = row[Posts.id].value,
                        userId = row[Posts.userId],
                        carModelId = row[Posts.carModelId],
                        imagePath = row[Posts.imagePath],
                        description = row[Posts.description],
                        createdAt = row[Posts.createdAt],
                        updatedAt = row[Posts.updatedAt]
                    )
                }
                .singleOrNull()
        }
    }

    override suspend fun getAllPosts(): List<Post> {
        return transaction {
            Posts
                .selectAll()
                .map { row ->
                    Post(
                        id = row[Posts.id].value,
                        userId = row[Posts.userId],
                        carModelId = row[Posts.carModelId],
                        imagePath = row[Posts.imagePath],
                        description = row[Posts.description],
                        createdAt = row[Posts.createdAt],
                        updatedAt = row[Posts.updatedAt]
                    )
                }
        }
    }

    override suspend fun getCurrentDayPostsForUser(userId: UUID, startTime: Instant, endTime: Instant): List<Post> {
        return transaction {
            Posts
                .selectAll()
                .where {
                    (Posts.userId eq userId) and
                    (Posts.createdAt greaterEq startTime) and
                    (Posts.createdAt less endTime)
                }
                .map { row ->
                    Post(
                        id = row[Posts.id].value,
                        userId = row[Posts.userId],
                        carModelId = row[Posts.carModelId],
                        imagePath = row[Posts.imagePath],
                        description = row[Posts.description],
                        createdAt = row[Posts.createdAt],
                        updatedAt = row[Posts.updatedAt]
                    )
                }
        }
    }


    override suspend fun editPost(postId: UUID, postText: String?): Int {
        return transaction {
            Posts.update({ Posts.id eq postId }) {
                it[description] = postText
                it[updatedAt] = Instant.now()
            }
        }
    }


    override suspend fun deletePost(postId: UUID): Int {
        return transaction {
            Posts
                .deleteWhere { id eq postId }
        }
    }

    override suspend fun getUserIdByPost(postId: UUID): UUID {
        return transaction {
            Posts
                .selectAll()
                .where { Posts.id eq postId }
                .mapNotNull { row ->
                    row[Posts.userId]
                }
                .singleOrNull() ?: throw IllegalArgumentException("Post with ID $postId not found")

        }
    }

}