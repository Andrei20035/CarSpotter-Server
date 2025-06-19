package com.carspotter.data.dao.user_car

import com.carspotter.data.model.User
import com.carspotter.data.model.UserCar
import com.carspotter.data.table.Users
import com.carspotter.data.table.UsersCars
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertReturning
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class UserCarDaoImpl : IUserCarDAO {
    override suspend fun createUserCar(userCar: UserCar): Int {
        return transaction {
            UsersCars.insertReturning(listOf(UsersCars.id)) {
                it[userId] = userCar.userId
                it[carModelId] = userCar.carModelId
                it[imagePath] = userCar.imagePath
            }.singleOrNull()?.get(UsersCars.id) ?: throw IllegalStateException("Failed to create user car")
        }
    }

    override suspend fun getUserCarById(userCarId: Int): UserCar? {
        return transaction {
            UsersCars
                .selectAll()
                .where { UsersCars.id eq userCarId }
                .mapNotNull { row ->
                    UserCar(
                        id = row[UsersCars.id],
                        userId = row[UsersCars.userId],
                        carModelId = row[UsersCars.carModelId],
                        imagePath = row[UsersCars.imagePath],
                        createdAt = row[UsersCars.createdAt],
                        updatedAt = row[UsersCars.updatedAt]
                    )
                }.singleOrNull()
        }
    }

    override suspend fun getUserCarByUserId(userId: Int): UserCar? {
        return transaction {
            UsersCars
                .selectAll()
                .where { UsersCars.userId eq userId }
                .mapNotNull { row ->
                    UserCar(
                        id = row[UsersCars.id],
                        userId = row[UsersCars.userId],
                        carModelId = row[UsersCars.carModelId],
                        imagePath = row[UsersCars.imagePath],
                        createdAt = row[UsersCars.createdAt],
                        updatedAt = row[UsersCars.updatedAt]
                    )
                }.singleOrNull()
        }
    }

    override suspend fun getUserByUserCarId(userCarId: Int): User {
        return transaction {
            (UsersCars innerJoin Users)
                .selectAll()
                .where { UsersCars.id eq userCarId }
                .mapNotNull { row ->
                    User(
                        id = row[Users.id],
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
                }.singleOrNull() ?: throw NoSuchElementException("User with userCarId: $userCarId not found")
        }
    }

    override suspend fun updateUserCar(userId: Int, imagePath: String?, carModelId: Int?): Int {
        return transaction {
            UsersCars.update({ UsersCars.userId eq userId }) { row ->
                if (imagePath != null) row[UsersCars.imagePath] = imagePath
                if (carModelId != null) row[UsersCars.carModelId] = carModelId
            }
        }
    }

    override suspend fun deleteUserCar(userId: Int): Int {
        return transaction {
            UsersCars.deleteWhere { UsersCars.userId eq userId }
        }
    }

    override suspend fun getAllUserCars(): List<UserCar> {
        return transaction {
            UsersCars
                .selectAll()
                .mapNotNull { row ->
                    UserCar(
                        userId = row[UsersCars.userId],
                        carModelId = row[UsersCars.carModelId],
                        imagePath = row[UsersCars.imagePath],
                    )
                }
        }
    }

}