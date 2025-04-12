package com.carspotter.data.model

data class AuthCredential(
    val id: Int = 0,
    val userId: Int,
    val email: String,
    val password: String?,
    val provider: String,
    val providerId: String?,
)