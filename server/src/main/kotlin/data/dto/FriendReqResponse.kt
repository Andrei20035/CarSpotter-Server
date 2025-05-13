package com.carspotter.data.dto

import com.carspotter.data.model.FriendRequest

data class FriendReqResponse(
    val senderId: Int,
    val receiverId: Int
)

fun FriendRequest.toResponse() = FriendReqResponse(
    senderId = this.senderId,
    receiverId = this.receiverId
)
