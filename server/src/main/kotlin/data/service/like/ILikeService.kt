package com.carspotter.data.service.like

import com.carspotter.data.model.User

interface ILikeService {
    suspend fun likePost(userId: Int, postId: Int)
    suspend fun unlikePost(userId: Int, postId: Int): Int
    suspend fun getLikesForPost(postId: Int): List<User>
}