package data.repository

import com.carspotter.data.model.AuthCredential
import com.carspotter.data.model.AuthProvider
import com.carspotter.data.model.User
import com.carspotter.data.repository.auth_credential.IAuthCredentialRepository
import com.carspotter.data.repository.user.IUserRepository
import com.carspotter.data.table.AuthCredentials
import com.carspotter.data.table.Users
import com.carspotter.di.daoModule
import com.carspotter.di.repositoryModule
import data.testutils.SchemaSetup
import data.testutils.TestDatabase
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import java.time.LocalDate
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserRepositoryTest: KoinTest {

    private val userRepository: IUserRepository by inject()
    private val authCredentialRepository: IAuthCredentialRepository by inject()

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
            modules(daoModule, repositoryModule)
        }

        SchemaSetup.createAuthCredentialsTableWithConstraint(AuthCredentials)
        SchemaSetup.createUsersTable(Users)
    }

    @BeforeEach
    fun clearDatabase() {
        transaction {
            AuthCredentials.deleteAll()

            runBlocking {
                credentialId1 = authCredentialRepository.createCredentials(
                    AuthCredential(
                        email = "test1@test.com",
                        password = null,
                        googleId = "231122",
                        provider = AuthProvider.GOOGLE
                    )
                )
                credentialId2 = authCredentialRepository.createCredentials(
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
        val userID = userRepository.createUser(
            User(
                authCredentialId = credentialId1,
                firstName = "Peter",
                lastName = "Parker",
                birthDate = LocalDate.of(2003, 11, 8),
                username = "Socate123",
                country = "USA"
            )
        )
        val retrievedUser = userRepository.getUserByID(userID)

        assertNotNull(retrievedUser)
        Assertions.assertEquals(userID, retrievedUser.id)
        assertEquals("Peter", retrievedUser.firstName)
        Assertions.assertEquals("Parker", retrievedUser.lastName)
        Assertions.assertEquals(null, retrievedUser.profilePicturePath)
        Assertions.assertEquals(LocalDate.of(2003, 11, 8), retrievedUser.birthDate)
        Assertions.assertEquals("Socate123", retrievedUser.username)
        Assertions.assertEquals("USA", retrievedUser.country)
        Assertions.assertEquals(0, retrievedUser.spotScore)

    }

    @Test
    fun `get user by username should return a list of users`() = runBlocking {
        userRepository.createUser(
            User(
                authCredentialId = credentialId1,
                firstName = "Peter",
                lastName = "Parker",
                birthDate = LocalDate.of(2003, 11, 8),
                username = "Socate123",
                country = "USA"
            )
        )

        userRepository.createUser(
            User(
                authCredentialId = credentialId2,
                firstName = "Mary Jane",
                lastName = "Watson",
                birthDate = LocalDate.of(2004, 4, 1),
                username = "Socate321",
                country = "USA"
            )
        )
        val retrievedUsers1 = userRepository.getUserByUsername("Socate")

        assertTrue(retrievedUsers1.isNotEmpty())
        assertEquals(2, retrievedUsers1.size)

        val retrievedUsers2 = userRepository.getUserByUsername("Socate1")

        assertTrue(retrievedUsers2.isNotEmpty())
        assertEquals(1, retrievedUsers2.size)
        assertEquals("Peter", retrievedUsers2[0].firstName)
    }

    @Test
    fun `get all users`() = runBlocking {
        val userId1 = userRepository.createUser(
            User(
                authCredentialId = credentialId1,
                firstName = "Peter",
                lastName = "Parker",
                birthDate = LocalDate.of(2003, 11, 8),
                username = "Socate123",
                country = "USA"
            )
        )

        val userId2 = userRepository.createUser(
            User(
                authCredentialId = credentialId2,
                firstName = "Mary Jane",
                lastName = "Watson",
                birthDate = LocalDate.of(2004, 4, 1),
                username = "Socate321",
                country = "USA"
            )
        )

        val users = userRepository.getAllUsers()

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
        val userId = userRepository.createUser(
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
        val userBeforeUpdate = userRepository.getUserByID(userId)
        assertNotNull(userBeforeUpdate)
        Assertions.assertNull(userBeforeUpdate.profilePicturePath)

        // Update profile picture
        userRepository.updateProfilePicture(userId, "/path/to/new/picture")

        val retrievedUser = userRepository.getUserByID(userId)

        // Make sure the user is not null after updating
        assertNotNull(retrievedUser)
        Assertions.assertEquals("/path/to/new/picture", retrievedUser.profilePicturePath)
    }

    @Test
    fun `delete user`() = runBlocking {
        val userId = userRepository.createUser(
            User(
                authCredentialId = credentialId1,
                firstName = "Peter",
                lastName = "Parker",
                birthDate = LocalDate.of(2003, 11, 8),
                username = "Socate123",
                country = "USA"
            )
        )

        val userBeforeDeletion = userRepository.getUserByID(userId)
        assertNotNull(userBeforeDeletion)

        userRepository.deleteUser(credentialId1)

        val userAfterDeletion = userRepository.getUserByID(userId)
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
