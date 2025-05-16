package com.carspotter.data.dto.request

data class UserCarUpdateRequest(
    val carModelId: Int,
    val imagePath: String? = null,
)
