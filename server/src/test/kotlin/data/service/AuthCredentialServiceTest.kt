package data.service

import com.carspotter.data.model.AuthCredential
import com.carspotter.data.model.AuthProvider
import com.carspotter.data.service.auth_credential.AuthCredentialServiceImpl
import com.carspotter.data.service.auth_credential.IAuthCredentialService
import com.carspotter.data.table.AuthCredentials
import com.carspotter.di.daoModule
import com.carspotter.di.repositoryModule
import com.carspotter.di.serviceModule
import data.testutils.SchemaSetup
import data.testutils.TestDatabase
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertNotNull
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.context.GlobalContext.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthCredentialServiceTest: KoinTest {

    private val authCredentialService: IAuthCredentialService by inject()

    @BeforeAll
    fun setup() {
        Database.connect(
            url = TestDatabase.postgresContainer.jdbcUrl,
            driver = "org.postgresql.Driver",
            user = TestDatabase.postgresContainer.username,
            password = TestDatabase.postgresContainer.password
        )

        startKoin {
            modules(daoModule, repositoryModule, serviceModule)
        }

        SchemaSetup.createAuthCredentialsTableWithConstraint(AuthCredentials)
    }

    @BeforeEach
    fun clearDatabase() {
        transaction {
            AuthCredentials.deleteAll()
        }
    }

    @Test
    fun `create and get credentials for login`() = runBlocking {
        val id = authCredentialService.createCredentials(
            AuthCredential(
                email = "repo@test.com",
                password = null,
                googleId = "gID123",
                provider = AuthProvider.GOOGLE
            )
        )

        val result = authCredentialService.getCredentialsForLogin("repo@test.com")

        assertNotNull(result)
        assertEquals(id, result.id)
        assertEquals(null, result.password)
        assertEquals("repo@test.com", result.email)
        assertEquals("gID123", result.googleId)
    }

    @Test
    fun `get credentials by ID returns correct DTO`() = runBlocking {
        val id = authCredentialService.createCredentials(
            AuthCredential(
                email = "dto@test.com",
                password = null,
                googleId = "dtoGID",
                provider = AuthProvider.GOOGLE
            )
        )

        val dto = authCredentialService.getCredentialsById(id)

        assertNotNull(dto)
        assertEquals("dto@test.com", dto.email)
        assertEquals("dtoGID", dto.providerId)
    }

    @Test
    fun `get credentials for regular login`() = runBlocking {
        val id = authCredentialService.createCredentials(
            AuthCredential(
                email = "dto@test.com",
                password = "passwordtest",
                googleId = null,
                provider = AuthProvider.REGULAR
            )
        )

        val authCredential = authCredentialService.regularLogin("dto@test.com", "passwordtest")

        assertNotNull(authCredential)
        assertEquals("dto@test.com", authCredential.email)
    }

    @Test
    fun `get credentials for google login`() = runBlocking {
        val id = authCredentialService.createCredentials(
            AuthCredential(
                email = "dto@test.com",
                password = null,
                googleId = "gid1",
                provider = AuthProvider.GOOGLE
            )
        )

        val authCredential = authCredentialService.googleLogin("dto@test.com", "gid1")

        assertNotNull(authCredential)
        assertEquals("dto@test.com", authCredential.email)
    }

    @Test
    fun `update password changes the stored password`() = runBlocking {
        val id = authCredentialService.createCredentials(
            AuthCredential(
                email = "update@test.com",
                password = "oldPass",
                googleId = null,
                provider = AuthProvider.REGULAR
            )
        )

        authCredentialService.updatePassword(id, "newPass")

        val result = authCredentialService.getCredentialsForLogin("update@test.com")
        assertNotNull(result)
        assertEquals("newPass", result.password)
    }

    @Test
    fun `get all credentials returns full list`() = runBlocking {
        authCredentialService.createCredentials(
            AuthCredential(
                email = "all1@test.com",
                password = null,
                googleId = "gid1",
                provider = AuthProvider.GOOGLE)
        )
        authCredentialService.createCredentials(
            AuthCredential(
                email = "all2@test.com",
                password = "passtest",
                googleId = null,
                provider = AuthProvider.REGULAR
            )
        )

        val all = authCredentialService.getAllCredentials()
        assertEquals(2, all.size)
    }

    @Test
    fun `delete credentials removes them from db`() = runBlocking {
        val id = authCredentialService.createCredentials(
            AuthCredential(
                email = "delete@test.com",
                password = null,
                googleId = "gid",
                provider = AuthProvider.GOOGLE
            )
        )

        val deleted = authCredentialService.deleteCredentials(id)
        val all = authCredentialService.getAllCredentials()

        assertEquals(1, deleted)
        assertTrue(all.none { it.email == "delete@test.com" })
    }

    @AfterAll
    fun tearDown() {
        transaction {
            SchemaUtils.drop(AuthCredentials)
        }
        stopKoin()
    }
}