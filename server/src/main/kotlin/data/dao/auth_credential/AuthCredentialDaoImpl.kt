package com.carspotter.data.dao.auth_credential

import com.carspotter.data.dao.auth_credentials.IAuthCredentialDAO
import com.carspotter.data.model.AuthCredential
import com.carspotter.data.model.AuthProvider
import com.carspotter.data.table.AuthCredentials
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertReturning
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class AuthCredentialDaoImpl : IAuthCredentialDAO {
    override suspend fun createCredentials(authCredential: AuthCredential): Int {
        return transaction {
            AuthCredentials.insertReturning(listOf(AuthCredentials.id)) {
                it[email] = authCredential.email
                it[password] = authCredential.password
                it[provider] = authCredential.provider.name
                it[googleId] = authCredential.googleId
            }.singleOrNull()?.get(AuthCredentials.id) ?: error("Failed to insert authCredential")
        }
    }

    override suspend fun getCredentialsForLogin(email: String): AuthCredential? {
        return transaction {
            AuthCredentials
                .selectAll()
                .where { AuthCredentials.email eq email }
                .mapNotNull { row ->
                    AuthCredential(
                        id = row[AuthCredentials.id],
                        email = row[AuthCredentials.email],
                        password = row[AuthCredentials.password],
                        provider = AuthProvider.valueOf(row[AuthCredentials.provider].uppercase()),
                        googleId = row[AuthCredentials.googleId]
                    )
                }
        }.singleOrNull()
    }

    override suspend fun getCredentialsById(credentialId: Int): AuthCredential? {
        return transaction {
            AuthCredentials
                .selectAll()
                .where { AuthCredentials.id eq credentialId }
                .mapNotNull { row ->
                    AuthCredential(
                        id = row[AuthCredentials.id],
                        email = row[AuthCredentials.email],
                        password = row[AuthCredentials.password],
                        provider = AuthProvider.valueOf(row[AuthCredentials.provider].uppercase()),
                        googleId = row[AuthCredentials.googleId]
                    )
                }
        }.singleOrNull()
    }

    override suspend fun updatePassword(credentialId: Int, newPassword: String): Int {
        return transaction {
            AuthCredentials
                .update ({AuthCredentials.id eq credentialId}) {
                    it[password] = newPassword
            }
        }
    }

    override suspend fun deleteCredentials(credentialId: Int): Int {
        return transaction {
            AuthCredentials
                .deleteWhere { id eq credentialId }
        }
    }

    override suspend fun getAllCredentials(): List<AuthCredential> {
        return transaction {
            AuthCredentials
                .selectAll()
                .mapNotNull { row ->
                    AuthCredential(
                        id = row[AuthCredentials.id],
                        email = row[AuthCredentials.email],
                        password = row[AuthCredentials.password],
                        provider = AuthProvider.valueOf(row[AuthCredentials.provider].uppercase()),
                        googleId = row[AuthCredentials.googleId]
                    )
                }
        }
    }

}