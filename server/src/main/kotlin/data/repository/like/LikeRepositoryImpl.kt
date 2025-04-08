package com.carspotter.data.repository.like

import com.carspotter.data.dao.like.LikeDaoImpl
import com.carspotter.data.model.User

class LikeRepositoryImpl(
    private val likeDao: LikeDaoImpl,
) : ILikeRepository {
    override suspend fun likePost(userId: Int, postId: Int) {
        likeDao.likePost(userId, postId)
    }

    override suspend fun unlikePost(userId: Int, postId: Int) {
        likeDao.unlikePost(userId, postId)
    }

    override suspend fun getLikesForPost(postId: Int): List<User> {
        return likeDao.getLikesForPost(postId)
    }
}