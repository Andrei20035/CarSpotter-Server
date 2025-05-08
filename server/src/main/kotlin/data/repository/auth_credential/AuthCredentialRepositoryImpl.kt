package com.carspotter.data.repository.auth_credentials

import com.carspotter.data.dao.auth_credentials.IAuthCredentialDAO
import com.carspotter.data.dto.AuthCredentialDTO
import com.carspotter.data.model.AuthCredential
import com.carspotter.data.repository.auth_credential.IAuthCredentialRepository

class AuthCredentialRepositoryImpl(
    private val authCredentialDao: IAuthCredentialDAO
): IAuthCredentialRepository {
    override suspend fun createCredentials(authCredential: AuthCredential): Int {
        return authCredentialDao.createCredentials(authCredential)
    }

    override suspend fun getCredentialsForLogin(email: String): AuthCredential? {
        return authCredentialDao.getCredentialsForLogin(email)
    }

    override suspend fun getCredentialsById(credentialId: Int): AuthCredentialDTO? {
        return authCredentialDao.getCredentialsById(credentialId)
    }

    override suspend fun updatePassword(credentialId: Int, newPassword: String): Int {
        return authCredentialDao.updatePassword(credentialId, newPassword)
    }

    override suspend fun deleteCredentials(credentialId: Int): Int {
        return authCredentialDao.deleteCredentials(credentialId)
    }

    override suspend fun getAllCredentials(): List<AuthCredentialDTO> {
        return authCredentialDao.getAllCredentials()
    }
}