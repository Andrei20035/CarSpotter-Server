package com.carspotter.data.service.post

import com.carspotter.data.dto.CreatePostDTO
import com.carspotter.data.dto.PostDTO
import com.carspotter.data.dto.response.FeedResponse
import com.carspotter.data.dto.toDTO
import com.carspotter.data.model.FeedCursor
import com.carspotter.data.model.Post
import com.carspotter.data.repository.post.IPostRepository
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

class PostServiceImpl(
    private val postRepository: IPostRepository
): IPostService {
    override suspend fun createPost(createPostDTO: CreatePostDTO): UUID {
        return try {
            postRepository.createPost(createPostDTO)
        } catch (e: IllegalStateException) {
            throw PostCreationException("Failed to create post for user ${createPostDTO.userId}", e)
        }
    }

    override suspend fun getPostById(postId: UUID): PostDTO? {
        return postRepository.getPostById(postId)?.toDTO()
    }

    override suspend fun getAllPosts(): List<PostDTO> {
        return postRepository.getAllPosts().map { it.toDTO() }
    }

    override suspend fun getCurrentDayPostsForUser(userId: UUID, userTimeZone: ZoneId): List<PostDTO> {
        val nowInUserTimeZone = ZonedDateTime.now(userTimeZone)
        val startOfDay = nowInUserTimeZone.toLocalDate().atStartOfDay(userTimeZone).toInstant()
        val endOfDay = nowInUserTimeZone.toLocalDate().atTime(23, 59, 59).atZone(userTimeZone).toInstant()

        val posts = postRepository.getCurrentDayPostsForUser(userId, startOfDay, endOfDay)

        return posts.map { it.toDTO() }
    }

    override suspend fun editPost(postId: UUID, postText: String?): Int {
        return postRepository.editPost(postId, postText)
    }

    override suspend fun deletePost(postId: UUID): Int {
        return postRepository.deletePost(postId)
    }

    override suspend fun getUserIdByPost(postId: UUID): UUID {
        return try {
            postRepository.getUserIdByPost(postId)
        } catch (e: IllegalStateException) {
            throw IllegalArgumentException("Cannot fetch user: post does not exist", e)
        }
    }

    override suspend fun getFeedPostsForUser(
        userId: UUID,
        latitude: Double?,
        longitude: Double?,
        radiusKm: Int?,
        limit: Int,
        cursor: FeedCursor?
    ): FeedResponse {
        return postRepository.getFeedPostsForUser(userId, latitude, longitude, radiusKm, limit, cursor)
    }
}

class PostCreationException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)