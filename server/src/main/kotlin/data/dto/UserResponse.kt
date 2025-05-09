package com.carspotter.data.dto

import com.carspotter.data.model.User
import java.time.Instant
import java.time.LocalDate

data class UserResponse(
    val id: Int = 0,
    val authCredentialId: Int = 0,
    val profilePicturePath: String? = null,
    val firstName: String,
    val lastName: String,
    val birthDate: LocalDate,
    val username: String,
    val country: String,
    val spotScore: Int = 0,
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null,
)

fun User.toResponse() = UserResponse(
    id = this.id,
    authCredentialId = this.authCredentialId,
    profilePicturePath = this.profilePicturePath,
    firstName = this.firstName,
    lastName = this.lastName,
    birthDate = this.birthDate,
    username = this.username,
    country = this.country,
    spotScore = this.spotScore,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
)

