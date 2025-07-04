package com.carspotter.data.service.friend

import com.carspotter.data.dto.FriendDTO
import com.carspotter.data.dto.UserDTO
import java.util.*

interface IFriendService {
    suspend fun addFriend(userId: UUID, friendId: UUID): UUID
    suspend fun getAllFriends(userId: UUID): List<UserDTO>
    suspend fun deleteFriend(userId: UUID, friendId: UUID): Int
    suspend fun getAllFriendsInDb(): List<FriendDTO>
}