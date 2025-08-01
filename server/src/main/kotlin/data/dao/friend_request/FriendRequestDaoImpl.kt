package com.carspotter.data.dao.friend_request

import com.carspotter.data.model.FriendRequest
import com.carspotter.data.model.User
import com.carspotter.data.table.FriendRequests
import com.carspotter.data.table.Friends
import com.carspotter.data.table.Users
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class FriendRequestDaoImpl : IFriendRequestDAO {
    override suspend fun sendFriendRequest(senderId: UUID, receiverId: UUID): UUID {
        return transaction {
            FriendRequests
                .insertReturning(listOf(FriendRequests.senderId, FriendRequests.receiverId)) {
                    it[this.senderId] = senderId
                    it[this.receiverId] = receiverId
                }.singleOrNull()?.get(FriendRequests.senderId) ?: throw IllegalStateException("Failed to add friend request to database")
        }
    }

    override suspend fun acceptFriendRequest(senderId: UUID, receiverId: UUID): Boolean {
        return transaction {
            val deletedRows = FriendRequests.deleteWhere {
                (FriendRequests.senderId eq senderId) and (FriendRequests.receiverId eq receiverId)
            }

            val firstInsert = Friends.insert {
                it[userId] = senderId
                it[friendId] = receiverId
            }
            val secondInsert = Friends.insert {
                it[userId] = receiverId
                it[friendId] = senderId
            }

            deletedRows == 1 && firstInsert.insertedCount == 1 && secondInsert.insertedCount == 1
        }
    }

    override suspend fun declineFriendRequest(senderId: UUID, receiverId: UUID): Int {
        return transaction {
            FriendRequests.deleteWhere {
                (FriendRequests.senderId eq senderId) and (FriendRequests.receiverId eq receiverId)
            }
        }
    }

    override suspend fun getAllFriendRequests(userId: UUID): List<User> {
        return transaction {
            // Query for friends where `userId` is the initiator
            val friendsAsInitiator = Users.alias("u1").let { usersAlias ->
                FriendRequests
                    .join(
                        usersAlias,
                        JoinType.INNER,
                        additionalConstraint = { FriendRequests.senderId eq usersAlias[Users.id] })
                    .selectAll()
                    .where { FriendRequests.receiverId eq userId }
                    .map { row ->
                        User(
                            id = row[usersAlias[Users.id]].value,
                            authCredentialId = row[usersAlias[Users.authCredentialId]],
                            profilePicturePath = row[usersAlias[Users.profilePicturePath]],
                            fullName = row[usersAlias[Users.fullName]],
                            phoneNumber =  row[usersAlias[Users.phoneNumber]],
                            birthDate = row[usersAlias[Users.birthDate]],
                            username = row[usersAlias[Users.username]],
                            country = row[usersAlias[Users.country]],
                            spotScore = row[usersAlias[Users.spotScore]],
                            createdAt = row[usersAlias[Users.createdAt]],
                            updatedAt = row[usersAlias[Users.updatedAt]],
                        )
                    }
            }

            // Query for friends where `userId` is the recipient
            val friendsAsRecipient = Users.alias("u2").let { usersAlias ->
                FriendRequests
                    .join(
                        usersAlias,
                        JoinType.INNER,
                        additionalConstraint = { FriendRequests.receiverId eq usersAlias[Users.id] })
                    .selectAll()
                    .where { FriendRequests.senderId eq userId }
                    .map { row ->
                        User(
                            id = row[usersAlias[Users.id]].value,
                            authCredentialId = row[usersAlias[Users.authCredentialId]],
                            fullName = row[usersAlias[Users.fullName]],
                            phoneNumber = row[usersAlias[Users.phoneNumber]],
                            profilePicturePath = row[usersAlias[Users.profilePicturePath]],
                            birthDate = row[usersAlias[Users.birthDate]],
                            username = row[usersAlias[Users.username]],
                            country = row[usersAlias[Users.country]],
                            spotScore = row[usersAlias[Users.spotScore]],
                            createdAt = row[usersAlias[Users.createdAt]],
                            updatedAt = row[usersAlias[Users.updatedAt]],
                        )
                    }
            }

            // Combine results and remove duplicates
            (friendsAsInitiator + friendsAsRecipient).distinctBy { it.id }
        }
    }

    override suspend fun getAllFriendReqFromDB(): List<FriendRequest> {
        return transaction {
            FriendRequests
                .selectAll()
                .mapNotNull { row ->
                    FriendRequest(
                        senderId = row[FriendRequests.senderId],
                        receiverId = row[FriendRequests.receiverId],
                        createdAt = row[FriendRequests.createdAt]
                    )
                }
        }
    }

}