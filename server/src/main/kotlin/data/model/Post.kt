package com.carspotter.data.model

import java.sql.Timestamp

data class Post(
    val id: Int = 0,
    val userId: Int,
    val carModelId: Int,
    val imagePath: String,
    val description: String? = null,
    val timestamp: Timestamp? = null,
)