package com.carspotter.data.dao.auth_credentials

import com.carspotter.data.dto.AuthCredentialDTO
import com.carspotter.data.model.AuthCredential

interface AuthCredentialDAO {
    suspend fun createCredentials(authCredential: AuthCredential): Int
    suspend fun getCredentialsForLogin(email: String): AuthCredential?
    suspend fun getCredentialsById(credentialId: Int): AuthCredentialDTO?
    suspend fun updatePassword(credentialId: Int, newHashedPassword: String): Int
    suspend fun deleteCredentials(credentialId: Int): Int
    suspend fun getAllCredentials(): List<AuthCredentialDTO>
}