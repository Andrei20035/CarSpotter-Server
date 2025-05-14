package com.carspotter.data.repository.friend_request

import com.carspotter.data.dao.friend_request.IFriendRequestDAO
import com.carspotter.data.model.FriendRequest
import com.carspotter.data.model.User

class FriendRequestRepositoryImpl(
    private val friendRequestDao: IFriendRequestDAO,
) : IFriendRequestRepository {
    override suspend fun sendFriendRequest(senderId: Int, receiverId: Int): Int {
        return friendRequestDao.sendFriendRequest(senderId, receiverId)
    }

    override suspend fun acceptFriendRequest(senderId: Int, receiverId: Int): Boolean {
        return friendRequestDao.acceptFriendRequest(senderId, receiverId)
    }

    override suspend fun declineFriendRequest(senderId: Int, receiverId: Int): Int {
        return friendRequestDao.declineFriendRequest(senderId, receiverId)
    }

    override suspend fun getAllFriendRequests(userId: Int): List<User> {
        return friendRequestDao.getAllFriendRequests(userId)
    }

    override suspend fun getAllFriendReqFromDB(): List<FriendRequest> {
        return friendRequestDao.getAllFriendReqFromDB()
    }
}