package com.carspotter.data.repository.friend

import com.carspotter.data.model.Friend
import com.carspotter.data.model.User

interface IFriendRepository {
    suspend fun addFriend(userId: Int, friendId: Int): Int
    suspend fun getAllFriends(userId: Int): List<User>
    suspend fun deleteFriend(userId: Int, friendId: Int)
    suspend fun getAllFriendsInDb(): List<Friend>
}