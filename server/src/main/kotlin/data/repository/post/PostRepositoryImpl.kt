package com.carspotter.data.repository.post

import com.carspotter.data.dao.post.PostDaoImpl
import com.carspotter.data.model.Post

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

    override suspend fun getCurrentDayPostsForUser(userId: Int): List<Post> {
        return postDao.getCurrentDayPostsForUser(userId)
    }

    override suspend fun deletePost(postId: Int) {
        postDao.deletePost(postId)
    }
}