package com.carspotter.data.dao.auth_credentials

import com.carspotter.data.model.AuthCredential

interface IAuthCredentialDAO {
    suspend fun createCredentials(authCredential: AuthCredential): Int
    suspend fun getCredentialsForLogin(email: String): AuthCredential?
    suspend fun getCredentialsById(credentialId: Int): AuthCredential?
    suspend fun updatePassword(credentialId: Int, newPassword: String): Int
    suspend fun deleteCredentials(credentialId: Int): Int
    suspend fun getAllCredentials(): List<AuthCredential>
}