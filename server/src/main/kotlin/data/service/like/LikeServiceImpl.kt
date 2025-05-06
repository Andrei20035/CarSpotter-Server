package com.carspotter.data.service.like

import com.carspotter.data.model.User
import com.carspotter.data.repository.like.ILikeRepository
import com.carspotter.data.repository.like.LikeRepositoryImpl

class LikeServiceImpl(
    private val likeRepository: ILikeRepository
): ILikeService {
    override suspend fun likePost(userId: Int, postId: Int) {
        likeRepository.likePost(userId, postId)
    }

    override suspend fun unlikePost(userId: Int, postId: Int): Int {
        return likeRepository.unlikePost(userId, postId)
    }

    override suspend fun getLikesForPost(postId: Int): List<User> {
        return likeRepository.getLikesForPost(postId)
    }
}