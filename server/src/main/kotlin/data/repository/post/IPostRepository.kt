package com.carspotter.data.repository.post

import com.carspotter.data.dto.CreatePostDTO
import com.carspotter.data.dto.response.FeedResponse
import com.carspotter.data.model.FeedCursor
import com.carspotter.data.model.Post
import java.time.Instant
import java.util.*

interface IPostRepository {
    suspend fun createPost(createPostDTO: CreatePostDTO): UUID
    suspend fun getPostById(postId: UUID): Post?
    suspend fun getAllPosts(): List<Post>
    suspend fun getCurrentDayPostsForUser(userId: UUID, startOfDay: Instant, endOfDay: Instant): List<Post>
    suspend fun editPost(postId: UUID, postText: String?): Int
    suspend fun deletePost(postId: UUID): Int
    suspend fun getUserIdByPost(postId: UUID): UUID
    suspend fun getFeedPostsForUser(userId: UUID, latitude: Double?, longitude: Double?, radiusKm: Int?, country: String, limit: Int, cursor: FeedCursor?): FeedResponse
}