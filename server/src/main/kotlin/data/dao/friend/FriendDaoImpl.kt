package com.carspotter.data.dao.friend

import com.carspotter.data.model.User
import com.carspotter.data.table.Friends
import com.carspotter.data.table.Users
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertReturning
import org.jetbrains.exposed.sql.transactions.transaction

class FriendDaoImpl: FriendDAO {
    override suspend fun addFriend(userId: Int, friendId: Int): Int {
        return transaction {
            Friends
                .insertReturning(listOf(Friends.friendId)) {
                    it[Friends.userId] = userId
                    it[Friends.friendId] = friendId
                }.singleOrNull()?.get(Friends.friendId) ?: error("Cannot add friend")

            Friends
                .insertReturning(listOf(Friends.friendId)) {
                    it[Friends.userId] = friendId
                    it[Friends.friendId] = userId
                }.singleOrNull()?.get(Friends.friendId) ?: error("Cannot add friend")

            friendId

        }
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
            (Friends innerJoin Users)
                .select (Friends.userId eq userId)
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