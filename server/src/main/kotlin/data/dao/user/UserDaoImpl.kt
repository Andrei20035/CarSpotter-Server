package com.carspotter.data.dao.user

import com.carspotter.data.model.User
import com.carspotter.data.table.AuthCredentials
import com.carspotter.data.table.Users
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction


class UserDaoImpl : UserDAO {
    override suspend fun createUser(user: User): Int {
        return transaction {
            addLogger(StdOutSqlLogger)

            Users.insertReturning(listOf(Users.id)) {
                it[authCredentialId] = user.authCredentialId
                it[profilePicturePath] = user.profilePicturePath
                it[firstName] = user.firstName
                it[lastName] = user.lastName
                it[birthDate] = user.birthDate
                it[username] = user.username
                it[country] = user.country
                it[spotScore] = user.spotScore
            }.singleOrNull()?.get(Users.id) ?: error("Failed to insert user")
        }
    }

    override suspend fun getUserByID(userId: Int): User? {
        return transaction {
            addLogger(StdOutSqlLogger)
            Users
                .selectAll()
                .where { Users.id eq userId }
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
                }.singleOrNull()
        }
    }

    override suspend fun getUserByUsername(username: String): User? {
        return transaction {
            addLogger(StdOutSqlLogger)
            Users
                .selectAll()
                .where { Users.username eq username }
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
                .singleOrNull()
        }
    }

    override suspend fun getAllUsers(): List<User> {
        return transaction {
            addLogger(StdOutSqlLogger)
            Users
                .selectAll()
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

    override suspend fun updateProfilePicture(userId: Int, imagePath: String): Int {
        return transaction {
            addLogger(StdOutSqlLogger)
            Users
                .update({ Users.id eq userId }) {
                    it[profilePicturePath] = imagePath
                }
        }
    }

    override suspend fun deleteUser(credentialId: Int): Int {
        return transaction {
            addLogger(StdOutSqlLogger)
            AuthCredentials.deleteWhere { id eq credentialId }
        }
    }

}