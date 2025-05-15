package com.carspotter.data.dto

import com.carspotter.data.model.Friend
import java.time.Instant

data class FriendDTO(
    val userId: Int,
    val friendId: Int,
    val createdAt: Instant? = null
)

fun Friend.toDTO() = FriendDTO(
    userId = this.userId,
    friendId = this.friendId,
    createdAt = this.createdAt
)
