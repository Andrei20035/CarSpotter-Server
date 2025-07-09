package com.carspotter.data.model

import com.carspotter.serialization.InstantSerializer
import com.carspotter.serialization.UUIDSerializer
import kotlinx.serialization.Serializable
import java.time.Instant
import java.util.UUID

@Serializable
data class FeedCursor(
    @Serializable(with = InstantSerializer::class)
    val lastCreatedAt: Instant,
    @Serializable(with = UUIDSerializer::class)
    val lastPostId: UUID
)
