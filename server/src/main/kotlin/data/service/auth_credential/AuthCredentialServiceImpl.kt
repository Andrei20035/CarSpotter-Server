package com.carspotter.data.service.auth_credential

import at.favre.lib.crypto.bcrypt.BCrypt
import com.carspotter.data.dto.AuthCredentialDTO
import com.carspotter.data.dto.toDTO
import com.carspotter.data.model.AuthCredential
import com.carspotter.data.model.AuthProvider
import com.carspotter.data.repository.auth_credential.IAuthCredentialRepository

class AuthCredentialServiceImpl(
    private val authCredentialRepository: IAuthCredentialRepository,
    private val googleTokenVerifier: GoogleTokenVerifier = GoogleTokenVerifierImpl()
) : IAuthCredentialService {
    override suspend fun createCredentials(authCredential: AuthCredential): Int {

        val existing = authCredentialRepository.getCredentialsForLogin(authCredential.email)

        if(existing != null) {
            throw IllegalArgumentException("Email is already registered")
        }

        val hashedPassword = when (authCredential.provider) {
            AuthProvider.REGULAR -> BCrypt.withDefaults().hashToString(12, authCredential.password?.toCharArray())
            AuthProvider.GOOGLE -> null
        }

        val authCredentialsToSave = authCredential.copy(password = hashedPassword)

        return try {
            authCredentialRepository.createCredentials(authCredentialsToSave)
        } catch (e: IllegalStateException) {
            throw CredentialCreationException("Unable to create credentials", e)
        }
    }

    override suspend fun regularLogin(email: String, password: String): AuthCredentialDTO? {
        val authCredential = authCredentialRepository.getCredentialsForLogin(email) ?: return null

        if (authCredential.provider == AuthProvider.REGULAR && BCrypt.verifyer()
                .verify(password.toCharArray(), authCredential.password).verified
        ) {
            return authCredential.toDTO()
        }
        return null
    }

    override suspend fun googleLogin(email: String, googleIdToken: String): AuthCredentialDTO? {
        val authCredential = authCredentialRepository.getCredentialsForLogin(email)
        val googleSub = googleTokenVerifier.verifyAndExtractSub(googleIdToken) ?: return null

        return when {
            authCredential != null &&
                    authCredential.provider == AuthProvider.GOOGLE &&
                    authCredential.googleId == googleSub -> {
                authCredential.toDTO()
            }

            authCredential != null &&
                    authCredential.provider != AuthProvider.GOOGLE -> {
                null
            }

            else -> {
                val newCredential = AuthCredential(
                    email = email,
                    password = null,
                    provider = AuthProvider.GOOGLE,
                    googleId = googleSub
                )
                authCredentialRepository.createCredentials(newCredential)
                newCredential.toDTO()
            }
        }
    }

    override suspend fun updatePassword(credentialId: Int, newPassword: String): Int {
        val newHashedPassword = BCrypt.withDefaults().hashToString(12, newPassword.toCharArray())
        return authCredentialRepository.updatePassword(credentialId, newHashedPassword)
    }

    override suspend fun deleteCredentials(credentialId: Int): Int {
        return authCredentialRepository.deleteCredentials(credentialId)
    }

    override suspend fun getCredentialsById(credentialId: Int): AuthCredential? {
        return authCredentialRepository.getCredentialsById(credentialId)
    }
}

class CredentialCreationException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)