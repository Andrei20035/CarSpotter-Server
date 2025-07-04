package com.carspotter.data.repository.friend

import com.carspotter.data.dao.friend.IFriendDAO
import com.carspotter.data.model.Friend
import com.carspotter.data.model.User
import java.util.*

class FriendRepositoryImpl(
    private val friendDao: IFriendDAO,
) : IFriendRepository {
    override suspend fun addFriend(userId: UUID, friendId: UUID): UUID {
        return friendDao.addFriend(userId, friendId)
    }

    override suspend fun getAllFriends(userId: UUID): List<User> {
        return friendDao.getAllFriends(userId)
    }

    override suspend fun deleteFriend(userId: UUID, friendId: UUID): Int {
        return friendDao.deleteFriend(userId, friendId)
    }

    override suspend fun getAllFriendsInDb(): List<Friend> {
        return friendDao.getAllFriendsInDb()
    }
}