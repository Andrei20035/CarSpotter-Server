package com.carspotter.di

import com.carspotter.data.service.auth_credential.JwtService
import com.carspotter.data.service.aws_S3.IStorageService
import com.carspotter.data.service.aws_S3.S3Service
import org.koin.dsl.module

val appModule = module {
    single {
        val jwtAudience = System.getenv("JWT_AUDIENCE") ?: throw IllegalStateException("JWT_AUDIENCE environment variable is not set")
        val jwtIssuer = System.getenv("JWT_ISSUER") ?: throw IllegalStateException("JWT_ISSUER environment variable is not set")
        val jwtSecret = System.getenv("JWT_SECRET") ?: throw IllegalStateException("JWT_SECRET environment variable is not set")

        JwtService(jwtSecret, jwtIssuer, jwtAudience)
    }

    single<IStorageService> {
        val bucket = System.getenv("AWS_S3_BUCKET") ?: throw IllegalStateException("AWS_S3_BUCKET environment variable is not set")
        val region = System.getenv("AWS_REGION") ?: throw IllegalStateException("AWS_REGION environment variable is not set")

        S3Service(bucketName = bucket, region = region)
    }
}