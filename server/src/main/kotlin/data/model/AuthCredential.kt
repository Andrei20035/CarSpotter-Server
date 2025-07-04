package com.carspotter.data.model

import java.util.*

data class AuthCredential(
    val id: UUID = UUID.randomUUID(),
    val email: String,
    val password: String?,
    val provider: AuthProvider,
    val googleId: String?,
)