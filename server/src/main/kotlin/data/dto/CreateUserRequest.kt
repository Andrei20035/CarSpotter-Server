package com.carspotter.data.dto

import com.carspotter.data.model.User
import java.time.LocalDate

data class CreateUserRequest(
    val authCredentialId: Int,
    val profilePicturePath: String? = null,
    val firstName: String,
    val lastName: String,
    val birthDate: LocalDate,
    val username: String,
    val country: String
)

fun CreateUserRequest.toUser() = User(
    authCredentialId = this.authCredentialId,
    profilePicturePath = this.profilePicturePath,
    firstName = this.firstName,
    lastName = this.lastName,
    birthDate = this.birthDate,
    username = this.username,
    country = this.country,
)
