package com.carspotter.data.dto

import com.carspotter.data.model.Friend
import com.carspotter.serialization.InstantSerializer
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class FriendDTO(
    val userId: Int,
    val friendId: Int,
    @Serializable(with = InstantSerializer::class)
    val createdAt: Instant? = null
)

fun Friend.toDTO() = FriendDTO(
    userId = this.userId,
    friendId = this.friendId,
    createdAt = this.createdAt
)
