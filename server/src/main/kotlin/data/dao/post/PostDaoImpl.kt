package com.carspotter.data.dao.post

import com.carspotter.data.model.Post
import com.carspotter.data.table.Posts
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greaterEq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.less
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate
import java.time.ZoneOffset

class PostDaoImpl: PostDAO {
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
                        description = row[Posts.description]
                    )
                }
                .singleOrNull()
        }
    }

    override suspend fun getAllPosts(): List<Post> {
        return transaction {
            addLogger(StdOutSqlLogger)
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
        val todayStartOfDayUTC = LocalDate.now(ZoneOffset.UTC).atStartOfDay().toInstant(ZoneOffset.UTC)
        val tomorrowStartOfDayUTC = LocalDate.now(ZoneOffset.UTC).plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC)

        return transaction {
            addLogger(StdOutSqlLogger)
            Posts
                .select (
                    (Posts.userId eq userId) and
                            (Posts.timestamp greaterEq todayStartOfDayUTC) and
                            (Posts.timestamp less tomorrowStartOfDayUTC)
                )
                .map { row ->
                    Post(
                        id = row[Posts.id],
                        userId = row[Posts.userId],
                        carModelId = row[Posts.carModelId],
                        imagePath = row[Posts.imagePath],
                        description = row[Posts.description],
                        timestamp = row[Posts.timestamp]
                    )
                }
        }
    }


    override suspend fun deletePost(postId: Int) {
        return transaction {
            addLogger(StdOutSqlLogger)
            Posts
                .deleteWhere { id eq postId }
        }
    }

}