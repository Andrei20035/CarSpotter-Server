package com.carspotter.data.repository.like

import com.carspotter.data.dao.like.ILikeDAO
import com.carspotter.data.model.User
import java.util.*

class LikeRepositoryImpl(
    private val likeDao: ILikeDAO,
) : ILikeRepository {
    override suspend fun likePost(userId: UUID, postId: UUID): UUID {
        return likeDao.likePost(userId, postId)
    }

    override suspend fun unlikePost(userId: UUID, postId: UUID): Int {
        return likeDao.unlikePost(userId, postId)
    }

    override suspend fun getLikesForPost(postId: UUID): List<User> {
        return likeDao.getLikesForPost(postId)
    }

    override suspend fun hasUserLikedPost(userId: UUID, postId: UUID): Boolean {
        return likeDao.hasUserLikedPost(userId, postId)
    }
}