package com.carspotter.data.service.auth_credential

import com.carspotter.data.dto.AuthCredentialDTO
import com.carspotter.data.model.AuthCredential
import java.util.*

interface IAuthCredentialService {
    suspend fun createCredentials(authCredential: AuthCredential): UUID
    suspend fun regularLogin(email: String, password: String): AuthCredentialDTO?
    suspend fun googleLogin(email: String, googleIdToken: String): AuthCredentialDTO?
    suspend fun updatePassword(credentialId: UUID, newPassword: String): Int
    suspend fun deleteCredentials(credentialId: UUID): Int
    suspend fun getCredentialsById(credentialId: UUID): AuthCredential?
}