package com.carspotter.data.service.friend_request

import com.carspotter.data.dto.FriendRequestDTO
import com.carspotter.data.dto.UserDTO
import java.util.*

interface IFriendRequestService {
    suspend fun sendFriendRequest(senderId: UUID, receiverId: UUID): UUID
    suspend fun acceptFriendRequest(senderId: UUID, receiverId: UUID): Boolean
    suspend fun declineFriendRequest(senderId: UUID, receiverId: UUID): Int
    suspend fun getAllFriendRequests(userId: UUID): List<UserDTO>
    suspend fun getAllFriendReqFromDB(): List<FriendRequestDTO>
}