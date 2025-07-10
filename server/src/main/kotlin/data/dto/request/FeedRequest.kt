package com.carspotter.data.dto.request

import com.carspotter.data.model.FeedCursor
import com.carspotter.serialization.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class FeedRequest(
    @Serializable(with = UUIDSerializer::class)
    val userId: UUID? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val radiusKm: Int? = null,
    val limit: Int,
    val cursor: FeedCursor? = null
)

fun FeedRequest.addId(userId: UUID): FeedRequest = FeedRequest(
    userId = userId,
    latitude = latitude,
    longitude= longitude,
    radiusKm = radiusKm,
    limit = limit,
    cursor = cursor
)


