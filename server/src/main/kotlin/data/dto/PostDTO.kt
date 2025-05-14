package com.carspotter.data.dto

import com.carspotter.data.model.Post
import com.carspotter.data.table.CarModels
import com.carspotter.data.table.Users
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant

data class PostDTO(
    val id: Int = 0,
    val userId: Int,
    val carModelId: Int,
    val imagePath: String,
    val description: String? = null,
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null,
)

fun Post.toDTO() = PostDTO(
    id = this.id,
    userId = this.id,
    carModelId = this.id,
    imagePath = this.imagePath,
    description = this.description,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt
)