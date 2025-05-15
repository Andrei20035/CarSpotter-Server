package com.carspotter.data.service.friend

import com.carspotter.data.dto.FriendDTO
import com.carspotter.data.dto.UserDTO
import com.carspotter.data.dto.toDTO
import com.carspotter.data.model.Friend
import com.carspotter.data.model.User
import com.carspotter.data.repository.friend.FriendRepositoryImpl
import com.carspotter.data.repository.friend.IFriendRepository
import com.carspotter.data.table.Friends

class FriendServiceImpl(
    private val friendRepository: IFriendRepository
): IFriendService {
    override suspend fun addFriend(userId: Int, friendId: Int): Int {
        return friendRepository.addFriend(userId, friendId)
    }

    override suspend fun getAllFriends(userId: Int): List<UserDTO> {
        return friendRepository.getAllFriends(userId).map { it.toDTO() }
    }

    override suspend fun deleteFriend(userId: Int, friendId: Int): Int {
        return friendRepository.deleteFriend(userId, friendId)
    }

    override suspend fun getAllFriendsInDb(): List<FriendDTO> {
        return friendRepository.getAllFriendsInDb().map { it.toDTO() }
    }
}