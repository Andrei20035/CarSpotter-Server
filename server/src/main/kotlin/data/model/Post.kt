package com.carspotter.data.model

import com.carspotter.data.table.Posts
import org.jetbrains.exposed.sql.ResultRow
import java.time.Instant
import java.util.*

data class Post(
    val id: UUID = UUID.randomUUID(),
    val userId: UUID,
    val carModelId: UUID,
    val imagePath: String,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val description: String? = null,
    val createdAt: Instant,
    val updatedAt: Instant,
)

fun ResultRow.toPost(): Post {
    return Post(
        id = this[Posts.id].value,
        userId = this[Posts.userId],
        carModelId = this[Posts.carModelId],
        imagePath = this[Posts.imagePath],
        description = this[Posts.description],
        latitude = this[Posts.latitude],
        longitude = this[Posts.longitude],
        createdAt = this[Posts.createdAt],
        updatedAt = this[Posts.updatedAt]
    )
}

fun Post.toCursor(): FeedCursor {
    return FeedCursor(
        lastCreatedAt = this.createdAt,
        lastPostId = this.id,
    )
}