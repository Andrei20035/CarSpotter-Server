package com.carspotter.data.dto

import com.carspotter.data.model.User
import java.time.Instant
import java.time.LocalDate

data class UserDTO(
    val id: Int = 0,
    val firstName: String,
    val lastName: String,
    val profilePicturePath: String? = null,
    val birthDate: LocalDate,
    val username: String,
    val country: String,
    val spotScore: Int = 0,
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null
)

fun User.toDTO(): UserDTO {
    return UserDTO(
        id = this.id,
        firstName = this.firstName,
        lastName = this.lastName,
        profilePicturePath = this.profilePicturePath,
        birthDate = this.birthDate,
        username = this.username,
        country = this.country,
        spotScore = this.spotScore,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}