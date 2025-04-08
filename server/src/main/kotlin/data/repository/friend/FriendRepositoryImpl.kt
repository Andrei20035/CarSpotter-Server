package com.carspotter.data.repository.friend

import com.carspotter.data.dao.friend.FriendDaoImpl
import com.carspotter.data.model.Friend
import com.carspotter.data.model.User

class FriendRepositoryImpl(
    private val friendDao: FriendDaoImpl,
) : IFriendRepository {
    override suspend fun addFriend(userId: Int, friendId: Int): Int {
        return friendDao.addFriend(userId, friendId)
    }

    override suspend fun getAllFriends(userId: Int): List<User> {
        return friendDao.getAllFriends(userId)
    }

    override suspend fun deleteFriend(userId: Int, friendId: Int) {
        friendDao.deleteFriend(userId, friendId)
    }

    override suspend fun getAllFriendsInDb(): List<Friend> {
        return friendDao.getAllFriendsInDb()
    }
}