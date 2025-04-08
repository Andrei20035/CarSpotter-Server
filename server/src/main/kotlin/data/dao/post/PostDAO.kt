package com.carspotter.data.dao.post

import com.carspotter.data.model.Post
import java.time.ZoneId

interface PostDAO {
    suspend fun createPost(post: Post): Int
    suspend fun getPostById(postId: Int): Post?
    suspend fun getAllPosts(): List<Post>
    suspend fun getCurrentDayPostsForUser(userId: Int, userTimeZone: ZoneId): List<Post>
    suspend fun editPost(postId: Int, postText: String): Int
    suspend fun deletePost(postId: Int)
}