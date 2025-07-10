package com.carspotter.data.dao.post

import com.carspotter.data.dto.CreatePostDTO
import com.carspotter.data.model.FeedCursor
import com.carspotter.data.model.FeedStage
import com.carspotter.data.model.Post
import com.carspotter.data.model.toPost
import com.carspotter.data.table.Friends
import com.carspotter.data.table.Posts
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greaterEq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.SqlExpressionBuilder.less
import org.jetbrains.exposed.sql.SqlExpressionBuilder.lessEq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.notInList
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.util.*
import kotlin.math.cos

class PostDaoImpl : IPostDAO {
    override suspend fun createPost(createPostDTO: CreatePostDTO): UUID {
        return transaction {
            Posts.insertReturning(listOf(Posts.id)) {
                it[userId] = createPostDTO.userId
                it[carModelId] = createPostDTO.carModelId
                it[imagePath] = createPostDTO.imagePath
                it[description] = createPostDTO.description
                it[longitude] = createPostDTO.longitude
                it[latitude] = createPostDTO.latitude
            }.singleOrNull()?.get(Posts.id)?.value ?: throw IllegalStateException("Failed to insert post")
        }
    }

    override suspend fun getPostById(postId: UUID): Post? {
        return transaction {
            Posts
                .selectAll()
                .where { Posts.id eq postId }
                .mapNotNull { row -> row.toPost() }
                .singleOrNull()
        }
    }

    override suspend fun getAllPosts(): List<Post> {
        return transaction {
            Posts
                .selectAll()
                .map { row -> row.toPost() }
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
                .map { row -> row.toPost() }
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

    override suspend fun getFriendPosts(
        friendIds: List<UUID>,
        after: Instant?,
        limit: Int
    ): List<Post> {
        println("getFriendPosts called with: friendIds=$friendIds, after=$after, limit=$limit")
        return transaction {
            if (friendIds.isEmpty()) {
                println("No friend IDs provided")
                return@transaction emptyList()
            }

            val condition = if (after != null) {
                (Posts.userId inList friendIds) and (Posts.createdAt less after)
            } else {
                Posts.userId inList friendIds
            }

            val posts = Posts
                .selectAll()
                .where{ condition }
                .orderBy(Posts.createdAt, SortOrder.DESC)
                .limit(limit)
                .map { it.toPost() }

            println("getFriendPosts returning ${posts.size} posts")
            return@transaction posts
        }
    }

    override suspend fun getNearbyPosts(
        excludedIds: List<UUID>,
        lat: Double,
        lon: Double,
        radiusKm: Int,
        after: Instant?,
        limit: Int
    ): List<Post> {
        println("getNearbyPosts called with: excludedIds=$excludedIds, lat=$lat, lon=$lon, radiusKm=$radiusKm, after=$after, limit=$limit")
        return transaction {
            val radiusDegrees = radiusKm / 111.0
            println("Calculated radius in degrees: $radiusDegrees")
            println("Search bounds: lat ${lat - radiusDegrees} to ${lat + radiusDegrees}, lon ${lon - radiusDegrees} to ${lon + radiusDegrees}")

            val condition = {
                (Posts.userId notInList excludedIds) and
                        (Posts.latitude greaterEq (lat - radiusDegrees)) and
                        (Posts.latitude lessEq (lat + radiusDegrees)) and
                        (Posts.longitude greaterEq (lon - radiusDegrees)) and
                        (Posts.longitude lessEq (lon + radiusDegrees))
            }

            val baseQuery = Posts
                .selectAll()
                .where { condition() }

            val filteredQuery = if (after != null) {
                baseQuery.andWhere { Posts.createdAt less after }
            } else baseQuery

            val posts = filteredQuery
                .orderBy(Posts.createdAt, SortOrder.DESC)
                .limit(limit)
                .map { it.toPost() }

            println("getNearbyPosts returning ${posts.size} posts")
            return@transaction posts
        }
    }

    override suspend fun getGlobalPosts(
        excludedIds: List<UUID>,
        after: Instant?,
        limit: Int
    ): List<Post> {
        println("getGlobalPosts called with: excludedIds=$excludedIds, after=$after, limit=$limit")
        return transaction {
            val baseCondition = Posts.userId notInList excludedIds
            val condition = if (after != null) {
                baseCondition and (Posts.createdAt less after)
            } else baseCondition

            val posts = Posts
                .selectAll()
                .where { condition }
                .orderBy(Posts.createdAt, SortOrder.DESC)
                .limit(limit)
                .map { it.toPost() }

            println("getGlobalPosts returning ${posts.size} posts")
            return@transaction posts
        }
    }
}