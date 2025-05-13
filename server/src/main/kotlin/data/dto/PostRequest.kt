package com.carspotter.data.dto

import com.carspotter.data.model.Post

data class PostRequest(
    val carModelId: Int,
    val imagePath: String,
    val description: String? = null,
)

fun PostRequest.toPost(userId: Int) = Post(
    carModelId = this.carModelId,
    imagePath = this.imagePath,
    description = this.description,
    userId = userId,
)
