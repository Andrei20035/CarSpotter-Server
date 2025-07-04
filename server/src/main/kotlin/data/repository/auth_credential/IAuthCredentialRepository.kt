package com.carspotter.data.repository.auth_credential

import com.carspotter.data.model.AuthCredential
import java.util.*

interface IAuthCredentialRepository {
    suspend fun createCredentials(authCredential: AuthCredential): UUID
    suspend fun getCredentialsForLogin(email: String): AuthCredential?
    suspend fun getCredentialsById(credentialId: UUID): AuthCredential?
    suspend fun updatePassword(credentialId: UUID, newPassword: String): Int
    suspend fun deleteCredentials(credentialId: UUID): Int
    suspend fun getAllCredentials(): List<AuthCredential>
}