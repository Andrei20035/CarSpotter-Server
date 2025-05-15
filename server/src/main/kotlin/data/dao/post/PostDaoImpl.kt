package com.carspotter.data.dao.post

import com.carspotter.data.model.Post
import com.carspotter.data.table.Posts
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

class PostDaoImpl : IPostDAO {
    override suspend fun createPost(post: Post): Int {
        return transaction {
            Posts.insertReturning(listOf(Posts.id)) {
                it[userId] = post.userId
                it[carModelId] = post.carModelId
                it[imagePath] = post.imagePath
                it[description] = post.description
            }.singleOrNull()?.get(Posts.id) ?: error("Failed to insert post")
        }
    }

    override suspend fun getPostById(postId: Int): Post? {
        return transaction {
            Posts
                .selectAll()
                .where { Posts.id eq postId }
                .mapNotNull { row ->
                    Post(
                        id = row[Posts.id],
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
                        id = row[Posts.id],
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

    override suspend fun getCurrentDayPostsForUser(userId: Int, startTime: Instant, endTime: Instant): List<Post> {
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
                        id = row[Posts.id],
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


    override suspend fun editPost(postId: Int, postText: String?): Int {
        return transaction {
            Posts.update({ Posts.id eq postId }) {
                it[description] = postText
                it[updatedAt] = Instant.now()
            }
        }
    }


    override suspend fun deletePost(postId: Int): Int {
        return transaction {
            Posts
                .deleteWhere { id eq postId }
        }
    }

    override suspend fun getUserIdByPost(postId: Int): Int {
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