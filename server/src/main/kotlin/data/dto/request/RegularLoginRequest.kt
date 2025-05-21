package com.carspotter.data.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class RegularLoginRequest(
    val email: String,
    val password: String
)
