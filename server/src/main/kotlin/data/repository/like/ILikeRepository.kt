package com.carspotter.data.repository.like

import com.carspotter.data.model.User

interface ILikeRepository {
    suspend fun likePost(userId: Int, postId: Int)
    suspend fun unlikePost(userId: Int, postId: Int): Int
    suspend fun getLikesForPost(postId: Int): List<User>
}