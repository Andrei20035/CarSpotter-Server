package com.carspotter.data.dto

import com.carspotter.data.model.AuthProvider

data class RegisterRequest(
    val email: String,
    val password: String?,
    val googleId: String?,
    val provider: AuthProvider
)
