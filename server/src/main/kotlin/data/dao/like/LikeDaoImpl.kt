package com.carspotter.data.dao.like

import com.carspotter.data.model.User
import com.carspotter.data.table.Likes
import com.carspotter.data.table.Users
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertReturning
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class LikeDaoImpl : ILikeDAO {
    override suspend fun likePost(userId: UUID, postId: UUID): UUID {
        return transaction {
            Likes
                .insertReturning(listOf(Likes.id)) {
                    it[Likes.userId] = userId
                    it[Likes.postId] = postId
                }.singleOrNull()?.get(Likes.id)?.value
                ?: throw IllegalStateException("Failed to insert like for user $userId and post $postId")
        }
    }

    override suspend fun unlikePost(userId: UUID, postId: UUID): Int {
        return transaction {
            Likes.deleteWhere {
                (Likes.userId eq userId) and (Likes.postId eq postId)
            }
        }
    }

    override suspend fun getLikesForPost(postId: UUID): List<User> {
        return transaction {
            (Likes innerJoin Users)
                .selectAll()
                .where { Likes.postId eq postId }
                .mapNotNull { row ->
                    User(
                        id = row[Users.id].value,
                        authCredentialId = row[Users.authCredentialId],
                        profilePicturePath = row[Users.profilePicturePath],
                        fullName = row[Users.fullName],
                        phoneNumber = row[Users.phoneNumber],
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

    override suspend fun hasUserLikedPost(userId: UUID, postId: UUID): Boolean {
        return transaction {
            Likes
                .selectAll()
                .where { (Likes.userId eq userId) and (Likes.postId eq postId) }
                .any()
        }
    }
}