package com.carspotter.data.model

import java.time.Instant
import java.util.*

data class Comment(
    val id: UUID = UUID.randomUUID(),
    val userId: UUID,
    val postId: UUID,
    val commentText: String,
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null,
)
