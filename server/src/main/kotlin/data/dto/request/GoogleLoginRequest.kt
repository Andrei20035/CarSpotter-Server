package com.carspotter.data.dto.request
import kotlinx.serialization.Serializable

@Serializable
data class GoogleLoginRequest(
    val email: String,
    val googleId: String
)
