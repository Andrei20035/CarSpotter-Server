package com.carspotter.data.model

import java.time.Instant
import java.util.*

data class FriendRequest(
    val senderId: UUID,
    val receiverId: UUID,
    val createdAt: Instant? = null,
)