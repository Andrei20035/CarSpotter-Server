package com.carspotter.data.model


import java.time.LocalDate


data class User (
    val id: Int = 0,
    val firstName: String,
    val lastName: String,
    val profilePicturePath: String? = null,
    val birthDate: LocalDate,
    val username: String,
    val password: String,
    val country: String,
    val spotScore: Int = 0
)
