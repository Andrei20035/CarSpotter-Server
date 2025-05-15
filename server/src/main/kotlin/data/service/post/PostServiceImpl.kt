package com.carspotter.data.service.post

import com.carspotter.data.dto.PostDTO
import com.carspotter.data.dto.toDTO
import com.carspotter.data.model.Post
import com.carspotter.data.repository.post.IPostRepository
import java.time.ZoneId
import java.time.ZonedDateTime

class PostServiceImpl(
    private val postRepository: IPostRepository
): IPostService {
    override suspend fun createPost(post: Post): Int {
        return postRepository.createPost(post)
    }

    override suspend fun getPostById(postId: Int): PostDTO? {
        return postRepository.getPostById(postId)?.toDTO()
    }

    override suspend fun getAllPosts(): List<PostDTO> {
        return postRepository.getAllPosts().map { it.toDTO() }
    }

    override suspend fun getCurrentDayPostsForUser(userId: Int, userTimeZone: ZoneId): List<PostDTO> {
        val nowInUserTimeZone = ZonedDateTime.now(userTimeZone)
        val startOfDay = nowInUserTimeZone.toLocalDate().atStartOfDay(userTimeZone).toInstant()
        val endOfDay = nowInUserTimeZone.toLocalDate().atTime(23, 59, 59).atZone(userTimeZone).toInstant()

        val posts = postRepository.getCurrentDayPostsForUser(userId, startOfDay, endOfDay)

        return posts.map { it.toDTO() }
    }

    override suspend fun editPost(postId: Int, postText: String?): Int {
        return postRepository.editPost(postId, postText)
    }

    override suspend fun deletePost(postId: Int): Int {
        return postRepository.deletePost(postId)
    }

    override suspend fun getUserIdByPost(postId: Int): Int {
        return postRepository.getUserIdByPost(postId)
    }
}