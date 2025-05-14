package com.carspotter.data.dto.response

import com.carspotter.data.model.Friend

data class FriendResponse(
    val userId: Int,
    val friendId: Int
)

fun Friend.toResponse() = FriendResponse(
    userId = this.userId,
    friendId = this.friendId
)