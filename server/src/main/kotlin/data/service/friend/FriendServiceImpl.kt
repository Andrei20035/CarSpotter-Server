package com.carspotter.data.service.friend

import com.carspotter.data.dto.FriendDTO
import com.carspotter.data.dto.UserDTO
import com.carspotter.data.dto.toDTO
import com.carspotter.data.repository.friend.IFriendRepository
import java.util.*

class FriendServiceImpl(
    private val friendRepository: IFriendRepository
): IFriendService {
    override suspend fun addFriend(userId: UUID, friendId: UUID): UUID {
        try {
            if(userId == friendId) throw IllegalArgumentException("Cannot add yourself as a friend")

            return friendRepository.addFriend(userId, friendId)
        } catch (e: IllegalStateException) {
            throw FriendshipCreationException("Failed to add friendship: $userId <-> $friendId", e)
        }
    }

    override suspend fun getAllFriends(userId: UUID): List<UserDTO> {
        return friendRepository.getAllFriends(userId).map { it.toDTO() }
    }

    override suspend fun deleteFriend(userId: UUID, friendId: UUID): Int {
        return friendRepository.deleteFriend(userId, friendId)
    }

    override suspend fun getAllFriendsInDb(): List<FriendDTO> {
        return friendRepository.getAllFriendsInDb().map { it.toDTO() }
    }
}

class FriendshipCreationException(message: String, error: Throwable? = null): Exception(message, error)