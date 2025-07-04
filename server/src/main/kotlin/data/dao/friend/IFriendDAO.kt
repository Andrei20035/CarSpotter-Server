package com.carspotter.data.dao.friend

import com.carspotter.data.model.Friend
import com.carspotter.data.model.User
import java.util.*

interface IFriendDAO {
    suspend fun addFriend(userId: UUID, friendId: UUID): UUID
    suspend fun getAllFriends(userId: UUID): List<User>
    suspend fun deleteFriend(userId: UUID, friendId: UUID): Int
    suspend fun getAllFriendsInDb(): List<Friend>
}