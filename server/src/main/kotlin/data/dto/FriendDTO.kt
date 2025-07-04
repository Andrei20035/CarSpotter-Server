package com.carspotter.data.dto

import com.carspotter.data.model.Friend
import com.carspotter.serialization.InstantSerializer
import com.carspotter.serialization.UUIDSerializer
import kotlinx.serialization.Serializable
import java.time.Instant
import java.util.*

@Serializable
data class FriendDTO(
    @Serializable(with = UUIDSerializer::class)
    val userId: UUID,
    @Serializable(with = UUIDSerializer::class)
    val friendId: UUID,
    @Serializable(with = InstantSerializer::class)
    val createdAt: Instant? = null
)

fun Friend.toDTO() = FriendDTO(
    userId = this.userId,
    friendId = this.friendId,
    createdAt = this.createdAt
)
