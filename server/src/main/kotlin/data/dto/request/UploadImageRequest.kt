package com.carspotter.data.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class UploadImageRequest(
    val imageName: String
)