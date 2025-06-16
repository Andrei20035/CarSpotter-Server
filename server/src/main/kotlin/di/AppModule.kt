package com.carspotter.di

import com.carspotter.data.service.auth_credential.JwtService
import org.koin.dsl.module

val appModule = module {
    single {
        val jwtAudience = System.getenv("JWT_AUDIENCE") ?: throw IllegalStateException("JWT_AUDIENCE environment variable is not set")
        val jwtIssuer = System.getenv("JWT_ISSUER") ?: throw IllegalStateException("JWT_ISSUER environment variable is not set")
        val jwtSecret = System.getenv("JWT_SECRET") ?: throw IllegalStateException("JWT_SECRET environment variable is not set")

        JwtService(jwtSecret, jwtIssuer, jwtAudience)
    }
}