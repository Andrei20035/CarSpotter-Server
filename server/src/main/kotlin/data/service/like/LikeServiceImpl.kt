package com.carspotter.data.service.like

import com.carspotter.data.dto.UserDTO
import com.carspotter.data.dto.toDTO
import com.carspotter.data.repository.like.ILikeRepository

class LikeServiceImpl(
    private val likeRepository: ILikeRepository
): ILikeService {
    override suspend fun likePost(userId: Int, postId: Int): Int {
        val exists = likeRepository.hasUserLikedPost(userId, postId)
        if(exists) return 0
        return likeRepository.likePost(userId, postId)
    }

    override suspend fun unlikePost(userId: Int, postId: Int): Int {
        val exists = likeRepository.hasUserLikedPost(userId, postId)
        if(!exists) return 0
        return likeRepository.unlikePost(userId, postId)
    }

    override suspend fun getLikesForPost(postId: Int): List<UserDTO> {
        return likeRepository.getLikesForPost(postId).map { it.toDTO() }
    }
}