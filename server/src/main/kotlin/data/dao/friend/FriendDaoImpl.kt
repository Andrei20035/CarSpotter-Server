package com.carspotter.data.dao.friend

import com.carspotter.data.model.Friend
import com.carspotter.data.model.User
import com.carspotter.data.table.Friends
import com.carspotter.data.table.Users
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class FriendDaoImpl : IFriendDAO {
    override suspend fun addFriend(userId: UUID, friendId: UUID): UUID {
        return transaction {
            val primaryInsert = insertFriend(userId, friendId)
            val secondaryInsert = insertFriend(friendId, userId)

            if (primaryInsert == null || secondaryInsert == null) {
                throw IllegalStateException("Failed to insert friendship: $userId <-> $friendId")
            }
            friendId
        }
    }

    private fun insertFriend(userId: UUID, friendId: UUID): UUID? {
        return Friends.insertReturning(listOf(Friends.friendId)) {
            it[Friends.userId] = userId
            it[Friends.friendId] = friendId
        }.singleOrNull()?.get(Friends.friendId)
    }

    override suspend fun deleteFriend(userId: UUID, friendId: UUID): Int {
        return transaction {
            Friends.deleteWhere {
                ((Friends.userId eq userId) and (Friends.friendId eq friendId)) or
                        ((Friends.userId eq friendId) and (Friends.friendId eq userId))
            }
        }
    }


    override suspend fun getAllFriends(userId: UUID): List<User> = transaction {
        val usersAlias = Users.alias("u")
        Friends
            .join(usersAlias, JoinType.INNER, additionalConstraint = { Friends.friendId eq usersAlias[Users.id] })
            .selectAll()
            .where { Friends.userId eq userId}
            .map { row ->
                User(
                    id = row[usersAlias[Users.id]].value,
                    authCredentialId = row[usersAlias[Users.authCredentialId]],
                    profilePicturePath = row[usersAlias[Users.profilePicturePath]],
                    fullName = row[usersAlias[Users.fullName]],
                    phoneNumber = row[usersAlias[Users.phoneNumber]],
                    birthDate = row[usersAlias[Users.birthDate]],
                    username = row[usersAlias[Users.username]],
                    country = row[usersAlias[Users.country]],
                    spotScore = row[usersAlias[Users.spotScore]],
                    createdAt = row[usersAlias[Users.createdAt]],
                    updatedAt = row[usersAlias[Users.updatedAt]],
                )
            }
    }


    override suspend fun getFriendIdsForUser(userId: UUID): List<UUID> = transaction {
        val friendsAsUser = Friends.select(Friends.friendId)
            .where { Friends.userId eq userId }
            .map { it[Friends.friendId] }

        val friendsAsFriend = Friends.select(Friends.userId)
            .where { Friends.friendId eq userId }
            .map { it[Friends.userId] }

        (friendsAsUser + friendsAsFriend).distinct()
    }



    override suspend fun getAllFriendsInDb(): List<Friend> {
        return transaction {
            Friends
                .selectAll()
                .mapNotNull { row ->
                    Friend(
                        userId = row[Friends.userId],
                        friendId = row[Friends.friendId],
                        createdAt = row[Friends.createdAt]
                    )
                }
        }
    }
}