package com.carspotter.data.dao.like

import com.carspotter.data.model.User
import java.util.*

interface ILikeDAO {
    suspend fun likePost(userId: UUID, postId: UUID): UUID
    suspend fun unlikePost(userId: UUID, postId: UUID): Int
    suspend fun getLikesForPost(postId: UUID): List<User>
    suspend fun hasUserLikedPost(userId: UUID, postId: UUID): Boolean
}