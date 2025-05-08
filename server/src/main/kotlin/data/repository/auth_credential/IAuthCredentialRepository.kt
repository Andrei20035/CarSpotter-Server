package com.carspotter.data.repository.auth_credential

import com.carspotter.data.dto.AuthCredentialDTO
import com.carspotter.data.model.AuthCredential

interface IAuthCredentialRepository {
    suspend fun createCredentials(authCredential: AuthCredential): Int
    suspend fun getCredentialsForLogin(email: String): AuthCredential?
    suspend fun getCredentialsById(credentialId: Int): AuthCredentialDTO?
    suspend fun updatePassword(credentialId: Int, newPassword: String): Int
    suspend fun deleteCredentials(credentialId: Int): Int
    suspend fun getAllCredentials(): List<AuthCredentialDTO>
}