package com.carspotter.data.service.post

import com.carspotter.data.dto.CreatePostDTO
import com.carspotter.data.dto.PostDTO
import com.carspotter.data.dto.response.FeedResponse
import com.carspotter.data.model.FeedCursor
import com.carspotter.data.model.Post
import java.time.ZoneId
import java.util.*

interface IPostService {
    suspend fun createPost(createPostDTO: CreatePostDTO): UUID
    suspend fun getPostById(postId: UUID): PostDTO?
    suspend fun getAllPosts(): List<PostDTO>
    suspend fun getCurrentDayPostsForUser(userId: UUID, userTimeZone: ZoneId): List<PostDTO>
    suspend fun editPost(postId: UUID, postText: String?): Int
    suspend fun deletePost(postId: UUID): Int
    suspend fun getUserIdByPost(postId: UUID): UUID
    suspend fun getFeedPostsForUser(userId: UUID, latitude: Double?, longitude: Double?, radiusKm: Int?, limit: Int, cursor: FeedCursor?): FeedResponse

}