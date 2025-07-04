package com.carspotter.data.dto

import com.carspotter.data.model.AuthCredential
import com.carspotter.data.model.AuthProvider
import java.util.*

data class AuthCredentialDTO(
    val id: UUID = UUID.randomUUID(),
    val email: String,
    val provider: AuthProvider,
)

fun AuthCredential.toDTO(): AuthCredentialDTO {
    return AuthCredentialDTO(
        id = this.id,
        email = this.email,
        provider = this.provider,
    )
}