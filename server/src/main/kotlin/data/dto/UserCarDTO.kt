package com.carspotter.data.dto

import com.carspotter.data.model.UserCar
import com.carspotter.serialization.InstantSerializer
import com.carspotter.serialization.UUIDSerializer
import kotlinx.serialization.Serializable
import java.time.Instant
import java.util.*

@Serializable
data class UserCarDTO(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID = UUID.randomUUID(),
    @Serializable(with = UUIDSerializer::class)
    val userId: UUID,
    @Serializable(with = UUIDSerializer::class)
    val carModelId: UUID,
    val imagePath: String? = null,
    @Serializable(with = InstantSerializer::class)
    val createdAt: Instant? = null,
    @Serializable(with = InstantSerializer::class)
    val updatedAt: Instant? = null
)

fun UserCar.toDTO() = UserCarDTO(
    id = this.id,
    userId = this.userId,
    carModelId = this.carModelId,
    imagePath = this.imagePath,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt
)
