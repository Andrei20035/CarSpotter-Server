package com.carspotter.di

import com.carspotter.data.service.auth_credential.JwtService
import io.github.cdimascio.dotenv.dotenv
import org.koin.dsl.module

val appModule = module {
    single {
        val dotenv = dotenv()
        val jwtAudience = System.getenv("JWT_AUDIENCE") ?: dotenv["JWT_AUDIENCE"] ?: throw IllegalStateException("JWT_AUDIENCE environment variable is not set")
        val jwtIssuer = System.getenv("JWT_ISSUER") ?: dotenv["JWT_ISSUER"] ?: throw IllegalStateException("JWT_ISSUER environment variable is not set")
        val jwtSecret = System.getenv("JWT_SECRET") ?: dotenv["JWT_SECRET"] ?: throw IllegalStateException("JWT_SECRET environment variable is not set")

        JwtService(jwtSecret, jwtIssuer, jwtAudience)
    }
}