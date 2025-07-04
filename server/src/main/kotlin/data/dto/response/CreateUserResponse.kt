package com.carspotter.data.dto.response

import com.carspotter.serialization.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class CreateUserResponse(
    val jwtToken: String,
    @Serializable(with = UUIDSerializer::class)
    val userId: UUID
)
