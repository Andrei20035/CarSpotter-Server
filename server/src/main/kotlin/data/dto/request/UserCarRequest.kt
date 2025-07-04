package com.carspotter.data.dto.request

import com.carspotter.data.model.UserCar
import com.carspotter.serialization.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class UserCarRequest(
    @Serializable(with = UUIDSerializer::class)
    val userId: UUID,
    @Serializable(with = UUIDSerializer::class)
    val carModelId: UUID,
    val imagePath: String? = null,
)

fun UserCarRequest.toUserCar(userId: UUID) = UserCar(
    userId = userId,
    carModelId = this.carModelId,
    imagePath = this.imagePath,
)
