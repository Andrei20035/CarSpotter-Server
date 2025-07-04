package com.carspotter.data.repository.friend_request

import com.carspotter.data.model.FriendRequest
import com.carspotter.data.model.User
import java.util.*

interface IFriendRequestRepository {
    suspend fun sendFriendRequest(senderId: UUID, receiverId: UUID): UUID
    suspend fun acceptFriendRequest(senderId: UUID, receiverId: UUID): Boolean
    suspend fun declineFriendRequest(senderId: UUID, receiverId: UUID): Int
    suspend fun getAllFriendRequests(userId: UUID): List<User>
    suspend fun getAllFriendReqFromDB(): List<FriendRequest>
}