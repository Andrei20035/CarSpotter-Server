package com.carspotter.data.repository.post

import com.carspotter.data.model.Post
import java.time.Instant

interface IPostRepository {
    suspend fun createPost(post: Post): Int
    suspend fun getPostById(postId: Int): Post?
    suspend fun getAllPosts(): List<Post>
    suspend fun getCurrentDayPostsForUser(userId: Int, startOfDay: Instant, endOfDay: Instant): List<Post>
    suspend fun editPost(postId: Int, postText: String?): Int
    suspend fun deletePost(postId: Int): Int
    suspend fun getUserIdByPost(postId: Int): Int
}