package com.carspotter.data.dto.request

import com.carspotter.data.model.AuthProvider
import kotlinx.serialization.Serializable

@Serializable
class LoginRequest (
    val email: String,
    val password: String? = null,
    val googleId: String? = null,
    val provider: AuthProvider
)