package com.carspotter.data.service.friend_request

import com.carspotter.data.dto.FriendRequestDTO
import com.carspotter.data.dto.UserDTO
import com.carspotter.data.dto.toDTO
import com.carspotter.data.repository.friend_request.IFriendRequestRepository

class FriendRequestServiceImpl(
    private val friendRequestRepository: IFriendRequestRepository
): IFriendRequestService {
    override suspend fun sendFriendRequest(senderId: Int, receiverId: Int): Int {
        try {
            if(senderId == receiverId) throw IllegalArgumentException("Cannot send friend request to yourself")

            return friendRequestRepository.sendFriendRequest(senderId, receiverId)
        } catch(e: IllegalStateException) {
            throw SendFriendRequestException("Failed to send friend request: $senderId <-> $receiverId", e)
        }
    }

    override suspend fun acceptFriendRequest(senderId: Int, receiverId: Int): Boolean {
        return friendRequestRepository.acceptFriendRequest(senderId, receiverId)
    }

    override suspend fun declineFriendRequest(senderId: Int, receiverId: Int): Int {
        return friendRequestRepository.declineFriendRequest(senderId, receiverId)
    }

    override suspend fun getAllFriendRequests(userId: Int): List<UserDTO> {
        return friendRequestRepository.getAllFriendRequests(userId).map { it.toDTO() }
    }

    override suspend fun getAllFriendReqFromDB(): List<FriendRequestDTO> {
        return friendRequestRepository.getAllFriendReqFromDB().map { it.toDTO() }
    }
}

class SendFriendRequestException(message: String, error: Throwable? = null): Exception(message, error)