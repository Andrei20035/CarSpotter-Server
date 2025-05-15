package com.carspotter.data.dto

import com.carspotter.data.model.FriendRequest
import java.time.Instant

data class FriendRequestDTO(
    val senderId: Int,
    val receiverId: Int,
    val createdAt: Instant? = null,
)

fun FriendRequest.toDTO() = FriendRequestDTO(
    senderId = this.senderId,
    receiverId = this.receiverId,
    createdAt = this.createdAt
)
