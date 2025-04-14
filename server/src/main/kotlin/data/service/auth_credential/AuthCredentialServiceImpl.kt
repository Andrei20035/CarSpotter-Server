package com.carspotter.data.service.auth_credential

import at.favre.lib.crypto.bcrypt.BCrypt
import com.carspotter.data.dto.AuthCredentialDTO
import com.carspotter.data.dto.toDTO
import com.carspotter.data.model.AuthCredential
import com.carspotter.data.model.AuthProvider
import com.carspotter.data.repository.auth_credentials.AuthCredentialRepositoryImpl

class AuthCredentialServiceImpl(
    private val authCredentialRepository: AuthCredentialRepositoryImpl
): IAuthCredentialService {
    override suspend fun createCredentials(authCredential: AuthCredential): Int {
        val hashedPassword = when (authCredential.provider) {
            AuthProvider.REGULAR -> BCrypt.withDefaults().hashToString(12, authCredential.password?.toCharArray())
            AuthProvider.GOOGLE -> null
        }

        val authCredentialsToSave = authCredential.copy(password = hashedPassword)

        return authCredentialRepository.createCredentials(authCredentialsToSave)
    }

    override suspend fun regularLogin(email: String, password: String): AuthCredentialDTO? {
        val authCredential = authCredentialRepository.getCredentialsForLogin(email) ?: return null

        if(authCredential.provider == AuthProvider.REGULAR && BCrypt.verifyer().verify(password.toCharArray(),authCredential.password).verified) {
            return authCredential.toDTO()
        }

        return null
    }

    override suspend fun googleLogin(email: String, googleId: String): AuthCredentialDTO? {
        val authCredential = authCredentialRepository.getCredentialsForLogin(email) ?: return null

        return if(authCredential.provider == AuthProvider.GOOGLE && authCredential.googleId == googleId) {
            authCredential.toDTO()
        } else {
            null
        }
    }

    override suspend fun getCredentialsForLogin(email: String): AuthCredential? {
        return authCredentialRepository.getCredentialsForLogin(email)
    }

    override suspend fun getCredentialsById(credentialId: Int): AuthCredentialDTO? {
        return authCredentialRepository.getCredentialsById(credentialId)
    }

    override suspend fun updatePassword(credentialId: Int, newHashedPassword: String): Int {
        return authCredentialRepository.updatePassword(credentialId, newHashedPassword)
    }

    override suspend fun deleteCredentials(credentialId: Int): Int {
        return authCredentialRepository.deleteCredentials(credentialId)
    }

    override suspend fun getAllCredentials(): List<AuthCredentialDTO> {
        return authCredentialRepository.getAllCredentials()
    }
}