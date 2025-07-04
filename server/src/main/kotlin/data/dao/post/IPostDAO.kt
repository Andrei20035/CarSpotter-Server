package com.carspotter.data.dao.post

import com.carspotter.data.model.Post
import java.time.Instant
import java.util.*

interface IPostDAO {
    suspend fun createPost(post: Post): UUID
    suspend fun getPostById(postId: UUID): Post?
    suspend fun getAllPosts(): List<Post>
    suspend fun getCurrentDayPostsForUser(userId: UUID, startTime: Instant, endTime: Instant): List<Post>
    suspend fun editPost(postId: UUID, postText: String?): Int
    suspend fun deletePost(postId: UUID): Int
    suspend fun getUserIdByPost(postId: UUID): UUID
}