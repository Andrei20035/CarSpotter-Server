package com.carspotter.data.dao.friend_request

import com.carspotter.data.model.User
import com.carspotter.data.table.FriendRequests
import com.carspotter.data.table.Friends
import com.carspotter.data.table.Users
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class FriendRequestDaoImpl : FriendRequestDAO {
    override suspend fun sendFriendRequest(senderId: Int, receiverId: Int): Int {
        return transaction {
            FriendRequests
                .insertReturning(listOf(FriendRequests.senderId, FriendRequests.receiverId)) {
                    it[this.senderId] = senderId
                    it[this.receiverId] = receiverId
                }.singleOrNull()?.get(FriendRequests.senderId) ?: error("Failed to add friend request to database")
        }
    }

    override suspend fun acceptFriendRequest(senderId: Int, receiverId: Int) {
        return transaction {
            FriendRequests.deleteWhere {
                (FriendRequests.senderId eq senderId) and (FriendRequests.receiverId eq receiverId)
            }

            Friends.insert {
                it[userId] = senderId
                it[friendId] = receiverId
            }
            Friends.insert {
                it[userId] = receiverId
                it[friendId] = senderId
            }
        }
    }

    override suspend fun declineFriendRequest(senderId: Int, receiverId: Int) {
        return transaction {
            val deletedRows = FriendRequests.deleteWhere {
                (FriendRequests.senderId eq senderId) and (FriendRequests.receiverId eq receiverId)
            }

            if (deletedRows == 0) {
                error("Friend request not found or already declined")
            }
        }
    }

    override suspend fun getAllFriendRequests(userId: Int): List<User> {
        return transaction {
            (FriendRequests innerJoin Users)
                .select((FriendRequests.receiverId eq userId) or (FriendRequests.senderId eq userId))
                .mapNotNull { row ->
                    User(
                        id = row[Users.id],
                        firstName = row[Users.firstName],
                        lastName = row[Users.lastName],
                        profilePicturePath = row[Users.profilePicturePath],
                        birthDate = row[Users.birthDate],
                        username = row[Users.username],
                        country = row[Users.country],
                        password = row[Users.password],
                        spotScore = row[Users.spotScore]
                    )
                }
        }
    }

}