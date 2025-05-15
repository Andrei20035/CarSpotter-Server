package com.carspotter.data.dto.request

import com.carspotter.data.model.Post

data class PostRequest(
    val carModelId: Int,
    val imagePath: String,
    val description: String? = null,
)

fun PostRequest.toPost(userId: Int) = Post(
    userId = userId,
    carModelId = this.carModelId,
    imagePath = this.imagePath,
    description = this.description,
)
