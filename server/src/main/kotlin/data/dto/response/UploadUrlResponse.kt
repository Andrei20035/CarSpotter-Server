package com.carspotter.data.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class UploadUrlResponse(
    val uploadUrl: String,
    val publicUrl: String
)

