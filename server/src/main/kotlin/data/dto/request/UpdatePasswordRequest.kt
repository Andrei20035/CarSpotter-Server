package com.carspotter.data.dto.request

data class UpdatePasswordRequest(
    val credentialId: Int,
    val newPassword: String
)
