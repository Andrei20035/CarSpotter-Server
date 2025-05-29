package com.carspotter.data.service.auth_credential

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.Date

class JwtService(
    private val jwtSecret: String,
    private val jwtIssuer: String,
    private val jwtAudience: String
) {
    fun generateJwtToken(credentialId: Int, userId: Int? = null, email: String, isAdmin: Boolean = false): Map<String, String> {
        val token = JWT.create()
            .withAudience(jwtAudience)
            .withIssuer(jwtIssuer)
            .withClaim("credentialId", credentialId)
            .withClaim("email", email)
            .withClaim("userId", userId)
            .withClaim("isAdmin", isAdmin)
            .withExpiresAt(Date(System.currentTimeMillis() + 86400000)) // 24h expiration
            .sign(Algorithm.HMAC256(jwtSecret))

        return mapOf("token" to token)
    }
}