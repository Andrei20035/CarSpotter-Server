package com.carspotter.data.service.like

import com.carspotter.data.dto.UserDTO
import java.util.*

interface ILikeService {
    suspend fun likePost(userId: UUID, postId: UUID): UUID
    suspend fun unlikePost(userId: UUID, postId: UUID): Int
    suspend fun getLikesForPost(postId: UUID): List<UserDTO>
}