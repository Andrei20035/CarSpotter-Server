package com.carspotter.data.dto

import com.carspotter.data.model.FriendRequest
import com.carspotter.serialization.InstantSerializer
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class FriendRequestDTO(
    val senderId: Int,
    val receiverId: Int,
    @Serializable(with = InstantSerializer::class)
    val createdAt: Instant? = null,
)

fun FriendRequest.toDTO() = FriendRequestDTO(
    senderId = this.senderId,
    receiverId = this.receiverId,
    createdAt = this.createdAt
)
