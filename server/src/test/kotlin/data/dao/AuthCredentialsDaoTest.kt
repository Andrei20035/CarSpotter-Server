package data.dao

import com.carspotter.data.dao.auth_credentials.AuthCredentialDaoImpl
import com.carspotter.data.model.AuthCredential
import com.carspotter.data.table.AuthCredentials
import data.testutils.TestDatabase
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertTrue
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthCredentialsDaoTest {

    private lateinit var authCredentialsDao: AuthCredentialDaoImpl

    @BeforeAll
    fun setupDatabase() {
        Database.connect(
            url = TestDatabase.postgresContainer.jdbcUrl,
            driver = "org.postgresql.Driver",
            user = TestDatabase.postgresContainer.username,
            password = TestDatabase.postgresContainer.password
        )

        transaction {
            SchemaUtils.create(AuthCredentials)
        }

        authCredentialsDao = AuthCredentialDaoImpl()
    }

    @BeforeEach
    fun clearDatabase() {
        transaction {
            AuthCredentials.deleteAll()
        }
    }

    @Test
    fun `get credentials for login`() = runBlocking {
        val credentialID = authCredentialsDao.createCredentials(
            AuthCredential(
                email = "test@test.com",
                password = "test",
                providerId = "2311",
                provider = "google"
            )
        )

        val credentials = authCredentialsDao.getCredentialsForLogin("test@test.com")
        assertNotNull(credentials)
        assertEquals("test@test.com", credentials.email)
        assertEquals("test", credentials.password)
        assertEquals("2311", credentials.providerId)
        assertEquals("google", credentials.provider)
    }

    @Test
    fun `get credentials by ID`() = runBlocking {
        val credentialID = authCredentialsDao.createCredentials(
            AuthCredential(
                email = "test@test.com",
                password = "test",
                providerId = "2311",
                provider = "google"
            )
        )

        val credentials = authCredentialsDao.getCredentialsById(credentialID)
        assertNotNull(credentials)
        assertEquals("test@test.com", credentials.email)
        assertEquals("2311", credentials.providerId)
        assertEquals("google", credentials.provider)
    }

    @Test
    fun `update password`() = runBlocking {
        val credentialID = authCredentialsDao.createCredentials(
            AuthCredential(
                email = "test@test.com",
                password = "test",
                providerId = "2311",
                provider = "google"
            )
        )

        authCredentialsDao.updatePassword(credentialID, "newPassword")

        val credentials = authCredentialsDao.getCredentialsForLogin("test@test.com")
        assertNotNull(credentials)
        assertEquals("newPassword", credentials.password)
    }

    @Test
    fun `show all credentials`() = runBlocking {
        authCredentialsDao.createCredentials(
            AuthCredential(
                email = "test1@test.com",
                password = "test1",
                providerId = "2311",
                provider = "google"
            )
        )
        authCredentialsDao.createCredentials(
            AuthCredential(
                email = "test2@test.com",
                password = "test2",
                providerId = "231122",
                provider = "local"
            )
        )

        val allCredentials = authCredentialsDao.getAllCredentials()
        assertEquals(2, allCredentials.size)
    }

    @Test
    fun `delete credentials`() = runBlocking {
        val credentialID = authCredentialsDao.createCredentials(
            AuthCredential(
                email = "test@test.com",
                password = "test",
                providerId = "2311",
                provider = "google"
            )
        )

        val rowsDeleted = authCredentialsDao.deleteCredentials(credentialID)
        val allCredentials = authCredentialsDao.getAllCredentials()

        assertTrue(allCredentials.isEmpty())
        assertEquals(1, rowsDeleted)
    }



    @AfterAll
    fun tearDown() {
        transaction {
            SchemaUtils.drop(AuthCredentials)
        }
    }
}