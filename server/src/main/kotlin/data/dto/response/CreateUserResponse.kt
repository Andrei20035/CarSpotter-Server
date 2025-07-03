package com.carspotter.data.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class CreateUserResponse(
    val jwtToken: String,
    val userId: Int
)
