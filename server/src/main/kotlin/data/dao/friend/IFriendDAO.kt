package com.carspotter.data.dao.friend

import com.carspotter.data.model.Friend
import com.carspotter.data.model.User

interface IFriendDAO {
    suspend fun addFriend(userId: Int, friendId: Int): Int
    suspend fun getAllFriends(userId: Int): List<User>
    suspend fun deleteFriend(userId: Int, friendId: Int): Int
    suspend fun getAllFriendsInDb(): List<Friend>
}