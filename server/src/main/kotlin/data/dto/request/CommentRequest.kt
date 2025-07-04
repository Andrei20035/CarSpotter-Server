package com.carspotter.data.dto.request

import com.carspotter.serialization.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class CommentRequest(
    @Serializable(with = UUIDSerializer::class)
    val postId: UUID,
    val commentText: String,
)
