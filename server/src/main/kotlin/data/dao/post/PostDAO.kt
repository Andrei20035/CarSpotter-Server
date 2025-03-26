package com.carspotter.data.dao.post

import com.carspotter.data.model.Post

interface PostDAO {
    suspend fun createPost(post: Post): Int
    suspend fun getPostById(postId: Int): Post?
    suspend fun getAllPosts(): List<Post>
    suspend fun getCurrentDayPostsForUser(userId: Int): List<Post>
    suspend fun deletePost(postId: Int)
}