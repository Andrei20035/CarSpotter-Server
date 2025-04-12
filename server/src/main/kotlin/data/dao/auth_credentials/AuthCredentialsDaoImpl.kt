package com.carspotter.data.dao.auth_credentials

import com.carspotter.data.model.AuthCredential
import com.carspotter.data.table.AuthCredentials
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.insertReturning
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class AuthCredentialsDaoImpl: AuthCredentialsDAO {
    override suspend fun createCredentials(authCredential: AuthCredential): Int {
        return transaction {
            addLogger(StdOutSqlLogger)
            AuthCredentials.
                    insertReturning(listOf(AuthCredentials.id)) {
                        it[userId] = authCredential.userId
                        it[email] = authCredential.email
                        it[password] = authCredential.password
                        it[provider] = authCredential.provider
                        it[providerId] = authCredential.providerId
                    }.singleOrNull()?.get(AuthCredentials.id) ?: error("Failed to insert authCredential")
        }
    }

    override suspend fun findByEmail(email: String): AuthCredential? {
        return transaction {
            addLogger(StdOutSqlLogger)
            AuthCredentials
                .selectAll()
                .where { AuthCredentials.email eq email }
                .mapNotNull { row ->
                    AuthCredential(
                        id = row[AuthCredentials.id],
                        userId = row[AuthCredentials.userId],
                        email = row[AuthCredentials.email],
                        password = row[AuthCredentials.password],
                        provider = row[AuthCredentials.provider],
                        providerId = row[AuthCredentials.providerId]
                    )
                }
        }.singleOrNull()
    }

    override suspend fun findByUserId(userId: Int): AuthCredential? {
        TODO("Not yet implemented")
    }

    override suspend fun updatePassword(email: String, newHashedPassword: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun deleteCredentials(userId: Int): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun showAllCredentials(): List<AuthCredential> {
        TODO("Not yet implemented")
    }

}