package com.carspotter.data.dao.auth_credentials

import com.carspotter.data.dto.AuthCredentialDTO
import com.carspotter.data.dto.toDTO
import com.carspotter.data.model.AuthCredential
import com.carspotter.data.table.AuthCredentials
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class AuthCredentialDaoImpl : AuthCredentialDAO {
    override suspend fun createCredentials(authCredential: AuthCredential): Int {
        return transaction {
            addLogger(StdOutSqlLogger)
            AuthCredentials.insertReturning(listOf(AuthCredentials.id)) {
                it[email] = authCredential.email
                it[password] = authCredential.password
                it[provider] = authCredential.provider
                it[providerId] = authCredential.providerId
            }.singleOrNull()?.get(AuthCredentials.id) ?: error("Failed to insert authCredential")
        }
    }

    override suspend fun getCredentialsForLogin(email: String): AuthCredential? {
        return transaction {
            addLogger(StdOutSqlLogger)
            AuthCredentials
                .selectAll()
                .where { AuthCredentials.email eq email }
                .mapNotNull { row ->
                    AuthCredential(
                        id = row[AuthCredentials.id],
                        email = row[AuthCredentials.email],
                        password = row[AuthCredentials.password],
                        provider = row[AuthCredentials.provider],
                        providerId = row[AuthCredentials.providerId]
                    )
                }
        }.singleOrNull()
    }

    override suspend fun getCredentialsById(credentialId: Int): AuthCredentialDTO? {
        return transaction {
            addLogger(StdOutSqlLogger)
            AuthCredentials
                .selectAll()
                .where { AuthCredentials.id eq credentialId }
                .mapNotNull { row ->
                    AuthCredential(
                        id = row[AuthCredentials.id],
                        email = row[AuthCredentials.email],
                        password = row[AuthCredentials.password],
                        provider = row[AuthCredentials.provider],
                        providerId = row[AuthCredentials.providerId]
                    ).toDTO()
                }
        }.singleOrNull()
    }

    override suspend fun updatePassword(credentialId: Int, newHashedPassword: String): Int {
        return transaction {
            addLogger(StdOutSqlLogger)
            AuthCredentials
                .update ({AuthCredentials.id eq credentialId}) {
                    it[password] = newHashedPassword
            }
        }
    }

    override suspend fun deleteCredentials(credentialId: Int): Int {
        return transaction {
            addLogger(StdOutSqlLogger)
            AuthCredentials
                .deleteWhere { id eq credentialId }
        }
    }

    override suspend fun getAllCredentials(): List<AuthCredentialDTO> {
        return transaction {
            addLogger(StdOutSqlLogger)
            AuthCredentials
                .selectAll()
                .mapNotNull { row ->
                    AuthCredential(
                        id = row[AuthCredentials.id],
                        email = row[AuthCredentials.email],
                        password = row[AuthCredentials.password],
                        provider = row[AuthCredentials.provider],
                        providerId = row[AuthCredentials.providerId]
                    ).toDTO()
                }
        }
    }

}