package com.carspotter.data.model

import com.carspotter.serialization.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class CarModel(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID = UUID.randomUUID(),
    val brand: String,
    val model: String,
    val startYear: Int,
    val endYear: Int,
)