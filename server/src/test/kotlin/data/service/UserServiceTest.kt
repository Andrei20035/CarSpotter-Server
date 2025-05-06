package data.service

import com.carspotter.data.model.AuthCredential
import com.carspotter.data.model.AuthProvider
import com.carspotter.data.model.User
import com.carspotter.data.service.auth_credential.IAuthCredentialService
import com.carspotter.data.service.user.IUserService
import com.carspotter.data.table.AuthCredentials
import com.carspotter.data.table.Users
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
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertNotNull
import org.junit.jupiter.api.assertNull
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import java.time.LocalDate
import kotlin.getValue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserServiceTest: KoinTest {

    private val userService: IUserService by inject()
    private val authCredentialService: IAuthCredentialService by inject()

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
            modules(daoModule, repositoryModule, serviceModule)
        }

        SchemaSetup.createAuthCredentialsTableWithConstraint(AuthCredentials)
        SchemaSetup.createUsersTable(Users)
    }

    @BeforeEach
    fun clearDatabase() {
        transaction {
            AuthCredentials.deleteAll()

            runBlocking {
                credentialId1 = authCredentialService.createCredentials(
                    AuthCredential(
                        email = "test1@test.com",
                        password = null,
                        googleId = "231122",
                        provider = AuthProvider.GOOGLE
                    )
                )
                credentialId2 = authCredentialService.createCredentials(
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
        val userID = userService.createUser(
            User(
                authCredentialId = credentialId1,
                firstName = "Peter",
                lastName = "Parker",
                birthDate = LocalDate.of(2003, 11, 8),
                username = "Socate123",
                country = "USA"
            )
        )
        val retrievedUser = userService.getUserByID(userID)

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
        val userID = userService.createUser(
            User(
                authCredentialId = credentialId1,
                firstName = "Peter",
                lastName = "Parker",
                birthDate = LocalDate.of(2003, 11, 8),
                username = "Socate123",
                country = "USA"
            )
        )
        val retrievedUser = userService.getUserByUsername("Socate123")

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
        val userId1 = userService.createUser(
            User(
                authCredentialId = credentialId1,
                firstName = "Peter",
                lastName = "Parker",
                birthDate = LocalDate.of(2003, 11, 8),
                username = "Socate123",
                country = "USA"
            )
        )

        val userId2 = userService.createUser(
            User(
                authCredentialId = credentialId2,
                firstName = "Mary Jane",
                lastName = "Watson",
                birthDate = LocalDate.of(2004, 4, 1),
                username = "Socate321",
                country = "USA"
            )
        )

        val users = userService.getAllUsers()

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
        val userId = userService.createUser(
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
        val userBeforeUpdate = userService.getUserByID(userId)
        assertNotNull(userBeforeUpdate)
        Assertions.assertNull(userBeforeUpdate.profilePicturePath)

        // Update profile picture
        userService.updateProfilePicture(userId, "/path/to/new/picture")

        val retrievedUser = userService.getUserByID(userId)

        // Make sure the user is not null after updating
        assertNotNull(retrievedUser)
        Assertions.assertEquals("/path/to/new/picture", retrievedUser.profilePicturePath)
    }

    @Test
    fun `delete user`() = runBlocking {
        val userId = userService.createUser(
            User(
                authCredentialId = credentialId1,
                firstName = "Peter",
                lastName = "Parker",
                birthDate = LocalDate.of(2003, 11, 8),
                username = "Socate123",
                country = "USA"
            )
        )

        val userBeforeDeletion = userService.getUserByID(userId)
        assertNotNull(userBeforeDeletion)

        userService.deleteUser(credentialId1)

        val userAfterDeletion = userService.getUserByID(userId)
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