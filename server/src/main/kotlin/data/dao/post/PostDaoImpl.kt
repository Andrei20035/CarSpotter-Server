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
            addLogger(StdOutSqlLogger)
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
            addLogger(StdOutSqlLogger)
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
            addLogger(StdOutSqlLogger)
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

    override suspend fun getCurrentDayPostsForUser(userId: Int, userTimeZone: ZoneId): List<Post> {
        // Get the user's current time in their local time zone
        val nowInUserTimeZone = ZonedDateTime.now(userTimeZone)

        // Get the start of today and the end of today in the user's local time zone
        val startOfDayInUserTimeZone = nowInUserTimeZone.toLocalDate().atStartOfDay(userTimeZone)
        val endOfDayInUserTimeZone = nowInUserTimeZone.toLocalDate().atTime(23, 59, 59).atZone(userTimeZone)

        // Convert the start and end of the day to UTC
        val startOfDayInUTC = startOfDayInUserTimeZone.toInstant()
        val endOfDayInUTC = endOfDayInUserTimeZone.toInstant()

        // Now use these UTC timestamps to filter the posts
        return transaction {
            addLogger(StdOutSqlLogger)
            Posts
                .selectAll()
                .where {
                    (Posts.userId eq userId) and
                            (Posts.createdAt greaterEq startOfDayInUTC) and
                            (Posts.createdAt less endOfDayInUTC)
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


    override suspend fun editPost(postId: Int, postText: String): Int {
        return transaction {
            addLogger(StdOutSqlLogger)
            Posts.update({ Posts.id eq postId }) {
                it[description] = postText
                it[updatedAt] = Instant.now()
            }
        }
    }


    override suspend fun deletePost(postId: Int): Int {
        return transaction {
            addLogger(StdOutSqlLogger)
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