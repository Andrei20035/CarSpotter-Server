package com.carspotter.data.model

data class AuthCredential(
    val id: Int = 0,
    val email: String,
    val password: String?,
    val provider: AuthProvider,
    val googleId: String?,
)