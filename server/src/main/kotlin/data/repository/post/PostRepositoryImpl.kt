package com.carspotter.data.repository.post

import com.carspotter.data.dao.post.IPostDAO
import com.carspotter.data.model.Post
import java.time.Instant
import java.util.*

class PostRepositoryImpl(
    private val postDao: IPostDAO,
) : IPostRepository {
    override suspend fun createPost(post: Post): UUID {
        return postDao.createPost(post)
    }

    override suspend fun getPostById(postId: UUID): Post? {
        return postDao.getPostById(postId)
    }

    override suspend fun getAllPosts(): List<Post> {
        return postDao.getAllPosts()
    }

    override suspend fun getCurrentDayPostsForUser(userId: UUID, startOfDay: Instant, endOfDay: Instant): List<Post> {
        return postDao.getCurrentDayPostsForUser(userId, startOfDay, endOfDay)
    }


    override suspend fun editPost(postId: UUID, postText: String?): Int {
        return postDao.editPost(postId, postText)
    }

    override suspend fun deletePost(postId: UUID): Int {
        return postDao.deletePost(postId)
    }

    override suspend fun getUserIdByPost(postId: UUID): UUID {
        return postDao.getUserIdByPost(postId)
    }
}