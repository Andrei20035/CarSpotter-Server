package com.carspotter.data.repository.friend_request

import com.carspotter.data.dao.friend_request.IFriendRequestDAO
import com.carspotter.data.model.FriendRequest
import com.carspotter.data.model.User
import java.util.*

class FriendRequestRepositoryImpl(
    private val friendRequestDao: IFriendRequestDAO,
) : IFriendRequestRepository {
    override suspend fun sendFriendRequest(senderId: UUID, receiverId: UUID): UUID {
        return friendRequestDao.sendFriendRequest(senderId, receiverId)
    }

    override suspend fun acceptFriendRequest(senderId: UUID, receiverId: UUID): Boolean {
        return friendRequestDao.acceptFriendRequest(senderId, receiverId)
    }

    override suspend fun declineFriendRequest(senderId: UUID, receiverId: UUID): Int {
        return friendRequestDao.declineFriendRequest(senderId, receiverId)
    }

    override suspend fun getAllFriendRequests(userId: UUID): List<User> {
        return friendRequestDao.getAllFriendRequests(userId)
    }

    override suspend fun getAllFriendReqFromDB(): List<FriendRequest> {
        return friendRequestDao.getAllFriendReqFromDB()
    }
}