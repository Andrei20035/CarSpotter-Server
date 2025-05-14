package com.carspotter.data.service.friend

import com.carspotter.data.dto.UserDTO
import com.carspotter.data.model.Friend
import com.carspotter.data.model.User
import com.carspotter.data.table.Friends

interface IFriendService {
    suspend fun addFriend(userId: Int, friendId: Int): Int
    suspend fun getAllFriends(userId: Int): List<UserDTO>
    suspend fun deleteFriend(userId: Int, friendId: Int): Int
    suspend fun getAllFriendsInDb(): List<Friend>
}