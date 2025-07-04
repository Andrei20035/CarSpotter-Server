package com.carspotter.data.service.post

import com.carspotter.data.dto.PostDTO
import com.carspotter.data.model.Post
import java.time.ZoneId
import java.util.*

interface IPostService {
    suspend fun createPost(post: Post): UUID
    suspend fun getPostById(postId: UUID): PostDTO?
    suspend fun getAllPosts(): List<PostDTO>
    suspend fun getCurrentDayPostsForUser(userId: UUID, userTimeZone: ZoneId): List<PostDTO>
    suspend fun editPost(postId: UUID, postText: String?): Int
    suspend fun deletePost(postId: UUID): Int
    suspend fun getUserIdByPost(postId: UUID): UUID
}