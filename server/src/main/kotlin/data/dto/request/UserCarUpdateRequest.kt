package com.carspotter.data.dto.request
import kotlinx.serialization.Serializable

@Serializable
data class UserCarUpdateRequest(
    val carModelId: Int,
    val imagePath: String? = null,
)
