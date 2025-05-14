package com.carspotter.data.dto.request

data class UpdatePasswordRequest(
    val credentialId: String,
    val newPassword: String
)
