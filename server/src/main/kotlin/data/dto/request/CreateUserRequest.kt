package com.carspotter.data.dto.request

import com.carspotter.data.model.User
import com.carspotter.serialization.LocalDateSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class CreateUserRequest(
    val profilePicturePath: String? = null,
    val fullName: String,
    val phoneNumber: String? = null,
    @Serializable(with = LocalDateSerializer::class)
    val birthDate: LocalDate,
    val username: String,
    val country: String
)

fun CreateUserRequest.toUser(credentialId: Int) = User(
    authCredentialId = credentialId,
    profilePicturePath = this.profilePicturePath,
    fullName = this.fullName,
    phoneNumber = this.phoneNumber,
    birthDate = this.birthDate,
    username = this.username,
    country = this.country,
)
