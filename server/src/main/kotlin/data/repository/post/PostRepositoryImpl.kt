package com.carspotter.data.repository.post

import com.carspotter.data.dao.post.PostDaoImpl
import com.carspotter.data.model.Post
import java.time.ZoneId

class PostRepositoryImpl(
    private val postDao: PostDaoImpl,
): IPostRepository {
    override suspend fun createPost(post: Post): Int {
        return postDao.createPost(post)
    }

    override suspend fun getPostById(postId: Int): Post? {
        return postDao.getPostById(postId)
    }

    override suspend fun getAllPosts(): List<Post> {
        return postDao.getAllPosts()
    }

    override suspend fun getCurrentDayPostsForUser(userId: Int, userTimeZone: ZoneId): List<Post> {
        return postDao.getCurrentDayPostsForUser(userId, userTimeZone)
    }

    override suspend fun deletePost(postId: Int) {
        postDao.deletePost(postId)
    }
}