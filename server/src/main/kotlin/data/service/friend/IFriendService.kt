package com.carspotter.data.service.friend

import com.carspotter.data.model.Friend
import com.carspotter.data.model.User

interface IFriendService {
    suspend fun addFriend(userId: Int, friendId: Int): Int
    suspend fun getAllFriends(userId: Int): List<User>
    suspend fun deleteFriend(userId: Int, friendId: Int)
    suspend fun getAllFriendsInDb(): List<Friend>
}