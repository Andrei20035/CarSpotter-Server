package com.carspotter.data.repository.post

import com.carspotter.data.model.Post
import java.time.Instant
import java.util.*

interface IPostRepository {
    suspend fun createPost(post: Post): UUID
    suspend fun getPostById(postId: UUID): Post?
    suspend fun getAllPosts(): List<Post>
    suspend fun getCurrentDayPostsForUser(userId: UUID, startOfDay: Instant, endOfDay: Instant): List<Post>
    suspend fun editPost(postId: UUID, postText: String?): Int
    suspend fun deletePost(postId: UUID): Int
    suspend fun getUserIdByPost(postId: UUID): UUID
}