package com.carspotter.data.dao.like

import com.carspotter.data.model.User
import com.carspotter.data.table.Likes
import com.carspotter.data.table.Users
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class LikeDaoImpl : ILikeDAO {
    override suspend fun likePost(userId: Int, postId: Int): Int {
        return transaction {
            addLogger(StdOutSqlLogger)
            Likes
                .insertReturning(listOf(Likes.id)) {
                    it[Likes.userId] = userId
                    it[Likes.postId] = postId
                }.singleOrNull()?.get(Likes.id) ?: error("Failed to insert user")
        }
    }

    override suspend fun unlikePost(userId: Int, postId: Int): Int {
        return transaction {
            addLogger(StdOutSqlLogger)
            Likes.deleteWhere {
                (Likes.userId eq userId) and (Likes.postId eq postId)
            }
        }
    }

    override suspend fun getLikesForPost(postId: Int): List<User> {
        return transaction {
            (Likes innerJoin Users)
                .selectAll()
                .where { Likes.postId eq postId }
                .mapNotNull { row ->
                    User(
                        id = row[Users.id],
                        authCredentialId = row[Users.authCredentialId],
                        profilePicturePath = row[Users.profilePicturePath],
                        firstName = row[Users.firstName],
                        lastName = row[Users.lastName],
                        birthDate = row[Users.birthDate],
                        username = row[Users.username],
                        country = row[Users.country],
                        spotScore = row[Users.spotScore],
                        createdAt = row[Users.createdAt],
                        updatedAt = row[Users.updatedAt]
                    )
                }
        }
    }
}