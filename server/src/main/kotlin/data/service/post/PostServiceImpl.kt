package com.carspotter.data.service.post

import com.carspotter.data.model.Post
import com.carspotter.data.repository.post.IPostRepository
import com.carspotter.data.repository.post.PostRepositoryImpl
import java.time.ZoneId

class PostServiceImpl(
    private val postRepository: IPostRepository
): IPostService {
    override suspend fun createPost(post: Post): Int {
        return postRepository.createPost(post)
    }

    override suspend fun getPostById(postId: Int): Post? {
        return postRepository.getPostById(postId)
    }

    override suspend fun getAllPosts(): List<Post> {
        return postRepository.getAllPosts()
    }

    override suspend fun getCurrentDayPostsForUser(userId: Int, userTimeZone: ZoneId): List<Post> {
        return postRepository.getCurrentDayPostsForUser(userId, userTimeZone)
    }

    override suspend fun editPost(postId: Int, postText: String): Int {
        return postRepository.editPost(postId, postText)
    }

    override suspend fun deletePost(postId: Int): Int {
        return postRepository.deletePost(postId)
    }
}