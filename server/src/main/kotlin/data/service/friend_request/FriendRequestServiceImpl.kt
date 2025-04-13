package com.carspotter.data.service.friend_request

import com.carspotter.data.model.FriendRequest
import com.carspotter.data.model.User
import com.carspotter.data.repository.friend_request.FriendRequestRepositoryImpl

class FriendRequestServiceImpl(
    private val friendRequestRepository: FriendRequestRepositoryImpl
): IFriendRequestService {
    override suspend fun sendFriendRequest(senderId: Int, receiverId: Int): Int {
        return friendRequestRepository.sendFriendRequest(senderId, receiverId)
    }

    override suspend fun acceptFriendRequest(senderId: Int, receiverId: Int) {
        friendRequestRepository.acceptFriendRequest(senderId, receiverId)
    }

    override suspend fun declineFriendRequest(senderId: Int, receiverId: Int): Int {
        return friendRequestRepository.declineFriendRequest(senderId, receiverId)
    }

    override suspend fun getAllFriendRequests(userId: Int): List<User> {
        return friendRequestRepository.getAllFriendRequests(userId)
    }

    override suspend fun getAllFriendReqFromDB(): List<FriendRequest> {
        return friendRequestRepository.getAllFriendReqFromDB()
    }
}