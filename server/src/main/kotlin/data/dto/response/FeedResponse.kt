package com.carspotter.data.dto.response

import com.carspotter.data.dto.PostDTO
import com.carspotter.data.model.FeedCursor
import com.carspotter.data.model.Post
import kotlinx.serialization.Serializable

@Serializable
data class FeedResponse(
    val posts: List<PostDTO>,
    val nextCursor: FeedCursor?,
    val hasMore: Boolean
)