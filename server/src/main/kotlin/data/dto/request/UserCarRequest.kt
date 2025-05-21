package com.carspotter.data.dto.request

import com.carspotter.data.model.UserCar
import kotlinx.serialization.Serializable

@Serializable
data class UserCarRequest(
    val userId: Int,
    val carModelId: Int,
    val imagePath: String? = null,
)

fun UserCarRequest.toUserCar(userId: Int) = UserCar(
    userId = userId,
    carModelId = this.carModelId,
    imagePath = this.imagePath,
)
