package com.carspotter.data.service.friend

import com.carspotter.data.model.Friend
import com.carspotter.data.model.User
import com.carspotter.data.repository.friend.FriendRepositoryImpl

class FriendServiceImpl(
    private val friendRepository: FriendRepositoryImpl
): IFriendService {
    override suspend fun addFriend(userId: Int, friendId: Int): Int {
        return friendRepository.addFriend(userId, friendId)
    }

    override suspend fun getAllFriends(userId: Int): List<User> {
        return friendRepository.getAllFriends(userId)
    }

    override suspend fun deleteFriend(userId: Int, friendId: Int) {
        friendRepository.deleteFriend(userId, friendId)
    }

    override suspend fun getAllFriendsInDb(): List<Friend> {
        return friendRepository.getAllFriendsInDb()
    }
}