package com.carspotter.data.dao.user

import com.carspotter.data.dto.UserDTO
import com.carspotter.data.dto.toDTO
import com.carspotter.data.model.User
import com.carspotter.data.table.Users
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction


class UserDaoImpl : UserDAO {
    override suspend fun createUser(user: User): Int {
        return transaction {
            addLogger(StdOutSqlLogger)

            Users.insertReturning(listOf(Users.id)) {
                it[firstName] = user.firstName
                it[lastName] = user.lastName
                it[profilePicturePath] = user.profilePicturePath
                it[birthDate] = user.birthDate
                it[username] = user.username
                it[password] = user.password
                it[country] = user.country
                it[spotScore] = user.spotScore
            }.singleOrNull()?.get(Users.id) ?: error("Failed to insert user")
        }
    }

    override suspend fun getUserByID(userId: Int): UserDTO? {
        return transaction {
            addLogger(StdOutSqlLogger)
            Users
                .selectAll()
                .where { Users.id eq userId }
                .mapNotNull { row ->
                    User(
                        id = row[Users.id],
                        firstName = row[Users.firstName],
                        lastName = row[Users.lastName],
                        profilePicturePath = row[Users.profilePicturePath],
                        birthDate = row[Users.birthDate],
                        username = row[Users.username],
                        password = row[Users.password],
                        country = row[Users.country],
                        spotScore = row[Users.spotScore],
                        createdAt = row[Users.createdAt],
                        updatedAt = row[Users.updatedAt]
                    ).toDTO()
                }.singleOrNull()
        }
    }

    override suspend fun getUserByUsername(username: String): UserDTO? {
        return transaction {
            addLogger(StdOutSqlLogger)
            Users
                .selectAll()
                .where { Users.username eq username }
                .mapNotNull { row ->
                    User(
                        id = row[Users.id],
                        firstName = row[Users.firstName],
                        lastName = row[Users.lastName],
                        profilePicturePath = row[Users.profilePicturePath],
                        birthDate = row[Users.birthDate],
                        username = row[Users.username],
                        password = row[Users.password],
                        country = row[Users.country],
                        spotScore = row[Users.spotScore],
                        createdAt = row[Users.createdAt],
                        updatedAt = row[Users.updatedAt]
                    ).toDTO()
                }
                .singleOrNull()
        }
    }

    override suspend fun getUserByEmail(email: String): User? {
        return transaction {
            addLogger(StdOutSqlLogger)
            Users
                .selectAll()
                .where { Users.email eq email }
                .mapNotNull { row ->
                    User(
                        id = row[Users.id],
                        firstName = row[Users.firstName],
                        lastName = row[Users.lastName],
                        profilePicturePath = row[Users.profilePicturePath],
                        birthDate = row[Users.birthDate],
                        username = row[Users.username],
                        password = row[Users.password], // exclude this from DTO
                        country = row[Users.country],
                        spotScore = row[Users.spotScore],
                        createdAt = row[Users.createdAt],
                        updatedAt = row[Users.updatedAt],
                        email = row[Users.email]  // Add email to your DTO
                    )
                }
                .singleOrNull()
        }
    }


    override suspend fun getAllUsers(): List<UserDTO> {
        return transaction {
            addLogger(StdOutSqlLogger)
            Users
                .selectAll()
                .mapNotNull { row ->
                    User(
                        id = row[Users.id],
                        firstName = row[Users.firstName],
                        lastName = row[Users.lastName],
                        profilePicturePath = row[Users.profilePicturePath],
                        birthDate = row[Users.birthDate],
                        username = row[Users.username],
                        password = row[Users.password],
                        country = row[Users.country],
                        spotScore = row[Users.spotScore],
                        createdAt = row[Users.createdAt],
                        updatedAt = row[Users.updatedAt]
                    ).toDTO()
                }
        }
    }

    override suspend fun updateProfilePicture(userId: Int, imagePath: String) {
        return transaction {
            addLogger(StdOutSqlLogger)
            Users
                .update({ Users.id eq userId }) {
                    it[profilePicturePath] = imagePath
                }
        }
    }

    override suspend fun deleteUser(userId: Int) {
        return transaction {
            addLogger(StdOutSqlLogger)
            Users
                .deleteWhere { id eq userId }
        }
    }

}