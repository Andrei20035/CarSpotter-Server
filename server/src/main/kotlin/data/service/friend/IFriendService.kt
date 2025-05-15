package com.carspotter.data.service.friend

import com.carspotter.data.dto.FriendDTO
import com.carspotter.data.dto.UserDTO

interface IFriendService {
    suspend fun addFriend(userId: Int, friendId: Int): Int
    suspend fun getAllFriends(userId: Int): List<UserDTO>
    suspend fun deleteFriend(userId: Int, friendId: Int): Int
    suspend fun getAllFriendsInDb(): List<FriendDTO>
}