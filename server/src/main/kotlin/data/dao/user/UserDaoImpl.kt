package com.carspotter.data.dao.user

import com.carspotter.data.model.User
import com.carspotter.data.table.AuthCredentials
import com.carspotter.data.table.Users
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*


class UserDaoImpl : IUserDAO {
    override suspend fun createUser(user: User): UUID {
        return transaction {
            Users.insertReturning(listOf(Users.id)) {
                it[authCredentialId] = user.authCredentialId
                it[profilePicturePath] = user.profilePicturePath
                it[fullName] = user.fullName
                it[phoneNumber] =  user.phoneNumber
                it[birthDate] = user.birthDate
                it[username] = user.username
                it[country] = user.country
                it[spotScore] = user.spotScore
            }.singleOrNull()?.get(Users.id)?.value ?: throw UserCreationException("Failed to insert user")
        }
    }

    override suspend fun getUserByID(userId: UUID): User? {
        return transaction {
            Users
                .selectAll()
                .where { Users.id eq userId }
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
                }.singleOrNull()
        }
    }

    override suspend fun getUserByUsername(username: String): List<User> {
        return transaction {
            Users
                .selectAll()
                .where { Users.username.lowerCase() like "${username.lowercase()}%" }
                .map { row ->
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

    override suspend fun getAllUsers(): List<User> {
        return transaction {
            Users
                .selectAll()
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

    override suspend fun updateProfilePicture(userId: UUID, imagePath: String): Int {
        return transaction {
            Users
                .update({ Users.id eq userId }) {
                    it[profilePicturePath] = imagePath
                }
        }
    }

    override suspend fun deleteUser(credentialId: UUID): Int {
        return transaction {
            AuthCredentials.deleteWhere { id eq credentialId }
        }
    }

}

class UserCreationException(message: String) : Exception(message)