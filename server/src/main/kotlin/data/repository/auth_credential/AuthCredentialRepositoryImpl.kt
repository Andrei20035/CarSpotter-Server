package com.carspotter.data.repository.auth_credentials

import com.carspotter.data.dao.auth_credential.IAuthCredentialDAO
import com.carspotter.data.model.AuthCredential
import com.carspotter.data.repository.auth_credential.IAuthCredentialRepository
import java.util.*

class AuthCredentialRepositoryImpl(
    private val authCredentialDao: IAuthCredentialDAO
): IAuthCredentialRepository {
    override suspend fun createCredentials(authCredential: AuthCredential): UUID {
        return authCredentialDao.createCredentials(authCredential)
    }

    override suspend fun getCredentialsForLogin(email: String): AuthCredential? {
        return authCredentialDao.getCredentialsForLogin(email)
    }

    override suspend fun getCredentialsById(credentialId: UUID): AuthCredential? {
        return authCredentialDao.getCredentialsById(credentialId)
    }

    override suspend fun updatePassword(credentialId: UUID, newPassword: String): Int {
        return authCredentialDao.updatePassword(credentialId, newPassword)
    }

    override suspend fun deleteCredentials(credentialId: UUID): Int {
        return authCredentialDao.deleteCredentials(credentialId)
    }

    override suspend fun getAllCredentials(): List<AuthCredential> {
        return authCredentialDao.getAllCredentials()
    }
}