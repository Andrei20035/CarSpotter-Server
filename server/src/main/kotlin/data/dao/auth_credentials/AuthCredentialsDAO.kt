package com.carspotter.data.dao.auth_credentials

import com.carspotter.data.model.AuthCredential

interface AuthCredentialsDAO {
    suspend fun createCredentials(authCredential: AuthCredential): Int
    suspend fun findByEmail(email: String): AuthCredential?
    suspend fun findByUserId(userId: Int): AuthCredential?
    suspend fun updatePassword(email: String, newHashedPassword: String): Boolean
    suspend fun deleteCredentials(userId: Int): Boolean
    suspend fun showAllCredentials(): List<AuthCredential>
}