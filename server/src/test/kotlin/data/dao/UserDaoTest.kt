package data.dao

import com.carspotter.data.dao.auth_credentials.IAuthCredentialDAO
import com.carspotter.data.dao.user.IUserDAO
import com.carspotter.data.model.AuthCredential
import com.carspotter.data.model.AuthProvider
import com.carspotter.data.model.User
import com.carspotter.data.table.AuthCredentials
import com.carspotter.data.table.Users
import com.carspotter.di.daoModule
import data.testutils.SchemaSetup
import data.testutils.TestDatabase
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.*
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import java.time.LocalDate

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserDaoTest: KoinTest {

    private val userDao: IUserDAO by inject()
    private val authCredentialDao: IAuthCredentialDAO by inject()

    private var credentialId1: Int = 0
    private var credentialId2: Int = 0

    @BeforeAll
    fun setupDatabase() {
        Database.connect(
            url = TestDatabase.postgresContainer.jdbcUrl,
            driver = "org.postgresql.Driver",
            user = TestDatabase.postgresContainer.username,
            password = TestDatabase.postgresContainer.password
        )

        startKoin {
            modules(daoModule)
        }

        SchemaSetup.createAuthCredentialsTableWithConstraint(AuthCredentials)
        SchemaSetup.createUsersTable(Users)
    }

    @BeforeEach
    fun clearDatabase() {
        transaction {
            AuthCredentials.deleteAll()

            runBlocking {
                credentialId1 = authCredentialDao.createCredentials(
                    AuthCredential(
                        email = "test1@test.com",
                        password = null,
                        googleId = "231122",
                        provider = AuthProvider.GOOGLE
                    )
                )
                credentialId2 = authCredentialDao.createCredentials(
                    AuthCredential(
                        email = "test2@test.com",
                        password = "test2",
                        googleId = null,
                        provider = AuthProvider.REGULAR
                    )
                )
            }
        }
    }

    @Test
    fun `get user by ID`() = runBlocking {
        val userID = userDao.createUser(
            User(
                authCredentialId = credentialId1,
                firstName = "Peter",
                lastName = "Parker",
                birthDate = LocalDate.of(2003, 11, 8),
                username = "Socate123",
                country = "USA"
            )
        )
        val retrievedUser = userDao.getUserByID(userID)

        assertNotNull(retrievedUser)
        Assertions.assertEquals(userID, retrievedUser.id)
        Assertions.assertEquals("Peter", retrievedUser.firstName)
        Assertions.assertEquals("Parker", retrievedUser.lastName)
        Assertions.assertEquals(null, retrievedUser.profilePicturePath)
        Assertions.assertEquals(LocalDate.of(2003, 11, 8), retrievedUser.birthDate)
        Assertions.assertEquals("Socate123", retrievedUser.username)
        Assertions.assertEquals("USA", retrievedUser.country)
        Assertions.assertEquals(0, retrievedUser.spotScore)

    }

    @Test
    fun `get user by username`() = runBlocking {
        val userID = userDao.createUser(
            User(
                authCredentialId = credentialId1,
                firstName = "Peter",
                lastName = "Parker",
                birthDate = LocalDate.of(2003, 11, 8),
                username = "Socate123",
                country = "USA"
            )
        )
        val retrievedUser = userDao.getUserByUsername("Socate123")

        assertNotNull(retrievedUser)
        Assertions.assertEquals(userID, retrievedUser.id)
        Assertions.assertEquals("Peter", retrievedUser.firstName)
        Assertions.assertEquals("Parker", retrievedUser.lastName)
        Assertions.assertEquals(null, retrievedUser.profilePicturePath)
        Assertions.assertEquals(LocalDate.of(2003, 11, 8), retrievedUser.birthDate)
        Assertions.assertEquals("Socate123", retrievedUser.username)
        Assertions.assertEquals("USA", retrievedUser.country)
        Assertions.assertEquals(0, retrievedUser.spotScore)

    }

    @Test
    fun `get all users`() = runBlocking {
        val userId1 = userDao.createUser(
            User(
                authCredentialId = credentialId1,
                firstName = "Peter",
                lastName = "Parker",
                birthDate = LocalDate.of(2003, 11, 8),
                username = "Socate123",
                country = "USA"
            )
        )

        val userId2 = userDao.createUser(
            User(
                authCredentialId = credentialId2,
                firstName = "Mary Jane",
                lastName = "Watson",
                birthDate = LocalDate.of(2004, 4, 1),
                username = "Socate321",
                country = "USA"
            )
        )

        val users = userDao.getAllUsers()

        Assertions.assertEquals(2, users.size)

        val user1 = users.find { it.id == userId1 }
        assertNotNull(user1)
        Assertions.assertEquals("Peter", user1.firstName)
        Assertions.assertEquals("Parker", user1.lastName)
        Assertions.assertEquals("Socate123", user1.username)
        Assertions.assertEquals(LocalDate.of(2003, 11, 8), user1.birthDate)
        Assertions.assertEquals("USA", user1.country)

        // Assert details of the second user
        val user2 = users.find { it.id == userId2 }
        assertNotNull(user2)
        Assertions.assertEquals("Mary Jane", user2.firstName)
        Assertions.assertEquals("Watson", user2.lastName)
        Assertions.assertEquals("Socate321", user2.username)
        Assertions.assertEquals(LocalDate.of(2004, 4, 1), user2.birthDate)
        Assertions.assertEquals("USA", user2.country)
    }

    @Test
    fun `update profile picture`() = runBlocking {
        val userId = userDao.createUser(
            User(
                authCredentialId = credentialId1,
                firstName = "Peter",
                lastName = "Parker",
                birthDate = LocalDate.of(2003, 11, 8),
                username = "Socate123",
                country = "USA"
            )
        )

        // Make sure user is not null before updating
        val userBeforeUpdate = userDao.getUserByID(userId)
        assertNotNull(userBeforeUpdate)
        Assertions.assertNull(userBeforeUpdate.profilePicturePath)

        // Update profile picture
        userDao.updateProfilePicture(userId, "/path/to/new/picture")

        val retrievedUser = userDao.getUserByID(userId)

        // Make sure the user is not null after updating
        assertNotNull(retrievedUser)
        Assertions.assertEquals("/path/to/new/picture", retrievedUser.profilePicturePath)
    }

    @Test
    fun `delete user`() = runBlocking {
        val userId = userDao.createUser(
            User(
                authCredentialId = credentialId1,
                firstName = "Peter",
                lastName = "Parker",
                birthDate = LocalDate.of(2003, 11, 8),
                username = "Socate123",
                country = "USA"
            )
        )

        val userBeforeDeletion = userDao.getUserByID(userId)
        assertNotNull(userBeforeDeletion)

        userDao.deleteUser(credentialId1)

        val userAfterDeletion = userDao.getUserByID(userId)
        assertNull(userAfterDeletion)
    }

    @AfterAll
    fun tearDown() {
        transaction {
            SchemaUtils.drop(Users, AuthCredentials)
        }
        stopKoin()
    }
}
