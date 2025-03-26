package com.carspotter.data.dao.friend

import com.carspotter.data.model.User

interface FriendDAO {
    suspend fun getAllFriends(userId: Int): List<User>
    suspend fun addFriend(userId: Int, friendId: Int): Int
    suspend fun deleteFriend(userId: Int, friendId: Int)
}