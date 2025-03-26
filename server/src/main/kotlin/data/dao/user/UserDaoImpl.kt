package com.carspotter.data.dao.user

import com.carspotter.data.model.User
import com.carspotter.data.table.Users
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.transactions.transaction

class UserDaoImpl : UserDAO {
    override suspend fun createUser(user: User): Int {
        return transaction {
            Users.insertReturning(listOf(Users.id)) {
                it[firstName] = user.firstName
                it[lastName] = user.lastName
                it[profilePicturePath] = user.profilePicturePath
                it[birthDate] = user.birthDate
                it[username] = user.username
                it[password] = user.password // Make sure to hash passwords!
                it[country] = user.country
                it[spotScore] = user.spotScore
            }.singleOrNull()?.get(Users.id) ?: error("Failed to insert user")
        }
    }

    override suspend fun getUserByID(userId: Int): User? {
        return transaction {
            Users
                .select(Users.id eq userId)
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
                        spotScore = row[Users.spotScore]
                    )
                }.singleOrNull()
        }
    }

    override suspend fun getUserByUsername(username: String): User? {
        return transaction {
            Users
                .select(Users.username eq username)
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
                        spotScore = row[Users.spotScore]
                    )
                }
                .singleOrNull()
        }
    }

    override suspend fun getAllUsers(): List<User> {
        return transaction {
            Users
                .selectAll()  // Select all rows from the Users table
                .mapNotNull { row -> // Map each row to a User object
                    User(
                        id = row[Users.id],
                        firstName = row[Users.firstName],
                        lastName = row[Users.lastName],
                        profilePicturePath = row[Users.profilePicturePath],
                        birthDate = row[Users.birthDate],
                        username = row[Users.username],
                        password = row[Users.password],
                        country = row[Users.country],
                        spotScore = row[Users.spotScore]
                    )
                }
        }
    }

    override suspend fun updateProfilePicture(userId: Int, imagePath: String) {
        return transaction {
            Users
                .update({ Users.id eq userId }) {
                    it[profilePicturePath] = imagePath
                }
        }
    }

    override suspend fun deleteUser(userId: Int) {
        return transaction {
            Users
                .deleteWhere { id eq userId }
        }
    }

}