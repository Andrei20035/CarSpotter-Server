package com.carspotter.data.dto.request
import kotlinx.serialization.Serializable

@Serializable
data class PostEditRequest(
    val newDescription: String? = null,
)
