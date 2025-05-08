package com.carspotter.data.dto

data class UpdatePasswordRequest(
    val credentialId: String,
    val newPassword: String
)
