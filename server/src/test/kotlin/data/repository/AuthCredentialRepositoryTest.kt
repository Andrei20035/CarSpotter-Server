package data.repository

import com.carspotter.data.dao.auth_credential.AuthCredentialDaoImpl
import com.carspotter.data.model.AuthCredential
import com.carspotter.data.model.AuthProvider
import com.carspotter.data.repository.auth_credentials.AuthCredentialRepositoryImpl
import com.carspotter.data.table.AuthCredentials
import data.testutils.TestDatabase
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthCredentialRepositoryTest {

    private lateinit var authCredentialRepository: AuthCredentialRepositoryImpl
    private lateinit var authCredentialDao: AuthCredentialDaoImpl

    @BeforeAll
    fun setup() {
        Database.connect(
            url = TestDatabase.postgresContainer.jdbcUrl,
            driver = "org.postgresql.Driver",
            user = TestDatabase.postgresContainer.username,
            password = TestDatabase.postgresContainer.password
        )

        transaction {
            SchemaUtils.create(AuthCredentials)
        }

        authCredentialDao = AuthCredentialDaoImpl()
        authCredentialRepository = AuthCredentialRepositoryImpl(authCredentialDao)
    }

    @BeforeEach
    fun clearDatabase() {
        transaction {
            AuthCredentials.deleteAll()
        }
    }

    @Test
    fun `create and get credentials for login`() = runBlocking {
        val id = authCredentialRepository.createCredentials(
            AuthCredential(
                email = "repo@test.com",
                password = "repoPass",
                googleId = "gID123",
                provider = AuthProvider.GOOGLE
            )
        )

        val result = authCredentialRepository.getCredentialsForLogin("repo@test.com")

        assertNotNull(result)
        assertEquals(id, result.id)
        assertEquals("repo@test.com", result.email)
        assertEquals("gID123", result.googleId)
    }

    @Test
    fun `get credentials by ID returns correct DTO`() = runBlocking {
        val id = authCredentialRepository.createCredentials(
            AuthCredential(
                email = "dto@test.com",
                password = "dtoPass",
                googleId = "dtoGID",
                provider = AuthProvider.GOOGLE
            )
        )

        val dto = authCredentialRepository.getCredentialsById(id)

        assertNotNull(dto)
        assertEquals("dto@test.com", dto.email)
        assertEquals("dtoGID", dto.providerId)
    }

    @Test
    fun `update password changes the stored password`() = runBlocking {
        val id = authCredentialRepository.createCredentials(
            AuthCredential(
                email = "update@test.com",
                password = "oldPass",
                googleId = "gid",
                provider = AuthProvider.GOOGLE
            )
        )

        authCredentialRepository.updatePassword(id, "newPass")

        val result = authCredentialRepository.getCredentialsForLogin("update@test.com")
        assertNotNull(result)
        assertEquals("newPass", result.password)
    }

    @Test
    fun `get all credentials returns full list`() = runBlocking {
        authCredentialRepository.createCredentials(
            AuthCredential(
                email = "all1@test.com",
                password = "pass1",
                googleId = "gid1",
                provider = AuthProvider.GOOGLE)
        )
        authCredentialRepository.createCredentials(
            AuthCredential(
                email = "all2@test.com",
                password = null,
                googleId = null,
                provider = AuthProvider.REGULAR
            )
        )

        val all = authCredentialRepository.getAllCredentials()
        assertEquals(2, all.size)
    }

    @Test
    fun `delete credentials removes them from db`() = runBlocking {
        val id = authCredentialRepository.createCredentials(
            AuthCredential(
                email = "delete@test.com",
                password = "pass",
                googleId = "gid",
                provider = AuthProvider.GOOGLE
            )
        )

        val deleted = authCredentialRepository.deleteCredentials(id)
        val all = authCredentialRepository.getAllCredentials()

        assertEquals(1, deleted)
        assertTrue(all.none { it.email == "delete@test.com" })
    }

    @AfterAll
    fun tearDown() {
        transaction {
            SchemaUtils.drop(AuthCredentials)
        }
    }
}