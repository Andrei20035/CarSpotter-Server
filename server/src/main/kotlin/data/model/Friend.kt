package com.carspotter.data.model

import java.time.Instant
import java.util.*

data class Friend(
    val userId: UUID,
    val friendId: UUID,
    val createdAt: Instant? = null
)