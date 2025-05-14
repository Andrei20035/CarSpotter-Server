package com.carspotter.data.repository.friend_request

import com.carspotter.data.model.FriendRequest
import com.carspotter.data.model.User

interface IFriendRequestRepository {
    suspend fun sendFriendRequest(senderId: Int, receiverId: Int): Int
    suspend fun acceptFriendRequest(senderId: Int, receiverId: Int): Boolean
    suspend fun declineFriendRequest(senderId: Int, receiverId: Int): Int
    suspend fun getAllFriendRequests(userId: Int): List<User>
    suspend fun getAllFriendReqFromDB(): List<FriendRequest>
}