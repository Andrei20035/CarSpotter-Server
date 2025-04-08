package com.carspotter.data.model

import java.time.Instant

data class FriendRequest(
    val senderId: Int,
    val receiverId: Int,
    val createdAt: Instant? = null,
)