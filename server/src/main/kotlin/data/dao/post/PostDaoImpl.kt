package com.carspotter.data.dao.post

import com.carspotter.data.model.Post
import com.carspotter.data.table.Posts
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greaterEq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.less
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertReturning
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate
import java.time.ZoneOffset

class PostDaoImpl: PostDAO {
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
                .select (Posts.id eq postId)
                .mapNotNull { row ->
                    Post(
                        id = row[Posts.id],
                        userId = row[Posts.userId],
                        carModelId = row[Posts.carModelId],
                        imagePath = row[Posts.imagePath],
                        description = row[Posts.description]
                    )
                }
                .singleOrNull() // Will return the post if found, or null if not found
        }
    }

    override suspend fun getAllPosts(): List<Post> {
        return transaction {
            Posts
                .selectAll() // Selects all posts
                .map { row ->
                    Post(
                        id = row[Posts.id],
                        userId = row[Posts.userId],
                        carModelId = row[Posts.carModelId],
                        imagePath = row[Posts.imagePath],
                        description = row[Posts.description]
                    )
                }
        }
    }

    override suspend fun getCurrentDayPostsForUser(userId: Int): List<Post> {
        val todayStartOfDay = LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC)
        val tomorrowStartOfDay = LocalDate.now().plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC)
        return transaction {
            Posts
                .select (
                    (Posts.userId eq userId) and
                            (Posts.timestamp greaterEq todayStartOfDay) and
                            (Posts.timestamp less tomorrowStartOfDay)
                )
                .map { row ->
                    Post(
                        id = row[Posts.id],
                        userId = row[Posts.userId],
                        carModelId = row[Posts.carModelId],
                        imagePath = row[Posts.imagePath],
                        description = row[Posts.description]
                    )
                }
        }
    }


    override suspend fun deletePost(postId: Int) {
        return transaction {
            Posts
                .deleteWhere { id eq postId }
        }
    }

}