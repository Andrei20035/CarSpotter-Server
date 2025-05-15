package com.carspotter.data.service.post

import com.carspotter.data.dto.PostDTO
import com.carspotter.data.model.Post
import java.time.ZoneId

interface IPostService {
    suspend fun createPost(post: Post): Int
    suspend fun getPostById(postId: Int): PostDTO?
    suspend fun getAllPosts(): List<PostDTO>
    suspend fun getCurrentDayPostsForUser(userId: Int, userTimeZone: ZoneId): List<PostDTO>
    suspend fun editPost(postId: Int, postText: String?): Int
    suspend fun deletePost(postId: Int): Int
    suspend fun getUserIdByPost(postId: Int): Int
}