package com.carspotter.data.dao.post

import com.carspotter.data.dto.CreatePostDTO
import com.carspotter.data.model.FeedCursor
import com.carspotter.data.model.Post
import java.time.Instant
import java.util.*

interface IPostDAO {
    suspend fun createPost(createPostDTO: CreatePostDTO): UUID
    suspend fun getPostById(postId: UUID): Post?
    suspend fun getAllPosts(): List<Post>
    suspend fun getCurrentDayPostsForUser(userId: UUID, startTime: Instant, endTime: Instant): List<Post>
    suspend fun editPost(postId: UUID, postText: String?): Int
    suspend fun deletePost(postId: UUID): Int
    suspend fun getUserIdByPost(postId: UUID): UUID
    suspend fun getFriendPosts(friendIds: List<UUID>, after: Instant? = null, limit: Int): List<Post>
    suspend fun getNearbyPosts(excludedIds: List<UUID>, lat: Double, lon: Double, radiusKm: Int, after: Instant? = null, limit: Int): List<Post>
    suspend fun getGlobalPosts(excludedIds: List<UUID>, after: Instant? = null, limit: Int): List<Post>
}