package com.carspotter.data.dto.request

import com.carspotter.data.model.User
import com.carspotter.serialization.LocalDateSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class CreateUserRequest(
    val profilePicturePath: String? = null,
    val firstName: String,
    val lastName: String,
    @Serializable(with = LocalDateSerializer::class)
    val birthDate: LocalDate,
    val username: String,
    val country: String
)

fun CreateUserRequest.toUser(credentialId: Int) = User(
    authCredentialId = credentialId,
    profilePicturePath = this.profilePicturePath,
    firstName = this.firstName,
    lastName = this.lastName,
    birthDate = this.birthDate,
    username = this.username,
    country = this.country,
)
