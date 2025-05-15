package com.carspotter.data.dto.request

import com.carspotter.data.model.AuthProvider

data class RegisterRequest(
    val email: String,
    val password: String?,
    val provider: AuthProvider
)
