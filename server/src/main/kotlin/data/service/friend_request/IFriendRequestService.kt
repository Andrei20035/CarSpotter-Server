package com.carspotter.data.service.friend_request

import com.carspotter.data.dto.FriendRequestDTO
import com.carspotter.data.dto.UserDTO

interface IFriendRequestService {
    suspend fun sendFriendRequest(senderId: Int, receiverId: Int): Int
    suspend fun acceptFriendRequest(senderId: Int, receiverId: Int): Boolean
    suspend fun declineFriendRequest(senderId: Int, receiverId: Int): Int
    suspend fun getAllFriendRequests(userId: Int): List<UserDTO>
    suspend fun getAllFriendReqFromDB(): List<FriendRequestDTO>
}