package com.carspotter.data.service.auth_credential

import com.carspotter.data.dto.AuthCredentialDTO
import com.carspotter.data.model.AuthCredential

interface IAuthCredentialService {
    suspend fun createCredentials(authCredential: AuthCredential): Int
    suspend fun regularLogin(email: String, password: String): AuthCredentialDTO?
    suspend fun googleLogin(email: String, googleId: String): AuthCredentialDTO?
    suspend fun updatePassword(credentialId: Int, newPassword: String): Int
    suspend fun deleteCredentials(credentialId: Int): Int
    suspend fun getCredentialsById(credentialId: Int): AuthCredential?
}