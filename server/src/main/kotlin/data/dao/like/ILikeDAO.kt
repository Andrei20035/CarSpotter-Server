package com.carspotter.data.dao.like

import com.carspotter.data.model.User

interface ILikeDAO {
    suspend fun likePost(userId: Int, postId: Int): Int
    suspend fun unlikePost(userId: Int, postId: Int): Int
    suspend fun getLikesForPost(postId: Int): List<User>
}