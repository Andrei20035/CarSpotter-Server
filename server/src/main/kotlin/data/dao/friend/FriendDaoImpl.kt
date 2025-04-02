package com.carspotter.data.dao.friend

import com.carspotter.data.model.Friend
import com.carspotter.data.model.User
import com.carspotter.data.table.Friends
import com.carspotter.data.table.Users
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class FriendDaoImpl: FriendDAO {
    override suspend fun addFriend(userId: Int, friendId: Int): Int {
        return transaction {
            addLogger(StdOutSqlLogger)
            val primaryInsert = insertFriend(userId, friendId)
            val secondaryInsert = insertFriend(friendId, userId)

            if (primaryInsert == null || secondaryInsert == null) {
                error("Failed to add one or both sides of the friendship")
            }
            friendId
        }
    }

    private fun insertFriend(userId: Int, friendId: Int): Int? {
        return Friends.insertReturning(listOf(Friends.friendId)) {
            it[Friends.userId] = userId
            it[Friends.friendId] = friendId
        }.singleOrNull()?.get(Friends.friendId)
    }

    override suspend fun deleteFriend(userId: Int, friendId: Int) {
        return transaction {
            Friends.deleteWhere {
                (Friends.userId eq userId) and (Friends.friendId eq friendId)
            }

            Friends.deleteWhere {
                (Friends.userId eq friendId) and (Friends.friendId eq userId)
            }
        }
    }

    override suspend fun getAllFriends(userId: Int): List<User> {
        return transaction {
            val friendsAsUser = (Friends innerJoin Users)
                .selectAll()
                .where { Friends.userId eq userId and (Friends.friendId eq Users.id) }
                .map { row ->
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

            val friendsAsFriend = (Friends innerJoin Users)
                .selectAll()
                .where { Friends.friendId eq userId and (Friends.userId eq Users.id) }
                .map { row ->
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

            // Combine results from both queries
            (friendsAsUser + friendsAsFriend).distinctBy { it.id }
        }
    }


    override suspend fun getAllFriendsInDb(): List<Friend> {
        return transaction {
            Friends
                .selectAll()
                .mapNotNull { row ->
                    Friend(
                        userId = row[Friends.userId],
                        friendId = row[Friends.friendId],
                    )
                }
        }
    }
}