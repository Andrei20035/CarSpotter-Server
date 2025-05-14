package com.carspotter.data.dto

import com.carspotter.data.model.AuthCredential
import com.carspotter.data.model.AuthProvider

data class AuthCredentialDTO(
    val id: Int = 0,
    val email: String,
    val provider: AuthProvider,
    val providerId: String?,
)

fun AuthCredential.toDTO(): AuthCredentialDTO {
    return AuthCredentialDTO(
        id = this.id,
        email = this.email,
        provider = this.provider,
        providerId = this.googleId
    )
}