package com.carspotter.data.dao.post

import com.carspotter.data.model.Post
import java.time.Instant

interface IPostDAO {
    suspend fun createPost(post: Post): Int
    suspend fun getPostById(postId: Int): Post?
    suspend fun getAllPosts(): List<Post>
    suspend fun getCurrentDayPostsForUser(userId: Int, startTime: Instant, endTime: Instant): List<Post>
    suspend fun editPost(postId: Int, postText: String?): Int
    suspend fun deletePost(postId: Int): Int
    suspend fun getUserIdByPost(postId: Int): Int
}