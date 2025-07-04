package com.carspotter.data.dto

import com.carspotter.data.model.FriendRequest
import com.carspotter.serialization.InstantSerializer
import com.carspotter.serialization.UUIDSerializer
import kotlinx.serialization.Serializable
import java.time.Instant
import java.util.*

@Serializable
data class FriendRequestDTO(
    @Serializable(with = UUIDSerializer::class)
    val senderId: UUID,
    @Serializable(with = UUIDSerializer::class)
    val receiverId: UUID,
    @Serializable(with = InstantSerializer::class)
    val createdAt: Instant? = null,
)

fun FriendRequest.toDTO() = FriendRequestDTO(
    senderId = this.senderId,
    receiverId = this.receiverId,
    createdAt = this.createdAt
)
