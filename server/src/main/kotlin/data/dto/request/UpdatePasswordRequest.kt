package com.carspotter.data.dto.request
import kotlinx.serialization.Serializable

@Serializable
data class UpdatePasswordRequest(
    val newPassword: String
)
