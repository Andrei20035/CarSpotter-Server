package data.repository

import com.carspotter.data.dao.auth_credential.AuthCredentialDaoImpl
import com.carspotter.data.dao.friend.FriendDaoImpl
import com.carspotter.data.dao.user.UserDaoImpl
import com.carspotter.data.model.AuthCredential
import com.carspotter.data.model.AuthProvider
import com.carspotter.data.model.User
import com.carspotter.data.repository.auth_credential.IAuthCredentialRepository
import com.carspotter.data.repository.friend.FriendRepositoryImpl
import com.carspotter.data.repository.friend.IFriendRepository
import com.carspotter.data.repository.user.IUserRepository
import com.carspotter.data.table.AuthCredentials
import com.carspotter.data.table.Friends
import com.carspotter.data.table.Users
import com.carspotter.di.daoModule
import com.carspotter.di.repositoryModule
import data.testutils.TestDatabase
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.*
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.java.KoinJavaComponent.inject
import org.koin.test.KoinTest
import org.koin.test.inject
import java.time.LocalDate

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FriendRepositoryTest: KoinTest {

    private val friendRepository: IFriendRepository by inject()
    private val authCredentialRepository: IAuthCredentialRepository by inject()
    private val userRepository: IUserRepository by inject()

    private var credentialId1: Int = 0
    private var credentialId2: Int = 0
    private var userId1: Int = 0
    private var userId2: Int = 0

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

        transaction {
            SchemaUtils.create(AuthCredentials, Users, Friends)
        }

        runBlocking {
            credentialId1 = authCredentialRepository.createCredentials(
                AuthCredential(
                    email = "test1@test.com",
                    password = "test1",
                    googleId = "231122",
                    provider = AuthProvider.GOOGLE
                )
            )
            credentialId2 = authCredentialRepository.createCredentials(
                AuthCredential(
                    email = "test2@test.com",
                    password = "test2",
                    googleId = "2311",
                    provider = AuthProvider.REGULAR
                )
            )
            userId1 = userRepository.createUser(
                User(
                    authCredentialId = credentialId1,
                    firstName = "Peter",
                    lastName = "Parker",
                    birthDate = LocalDate.of(2003, 11, 8),
                    username = "Socate123",
                    country = "USA"
                )
            )
            userId2 = userRepository.createUser(
                User(
                    authCredentialId = credentialId2,
                    firstName = "Mary Jane",
                    lastName = "Watson",
                    birthDate = LocalDate.of(2004, 4, 1),
                    username = "Socate321",
                    country = "USA"
                )
            )
        }
    }

    @BeforeEach
    fun clearDatabase() {
        transaction {
            Friends.deleteAll()
        }
    }

    @Test
    fun `add friend`() = runBlocking {
        val friendId = friendRepository.addFriend(userId1, userId2)

        Assertions.assertNotNull(friendId)

        val allFriendsInDb = friendRepository.getAllFriendsInDb()

        Assertions.assertEquals(2, allFriendsInDb.size)
        Assertions.assertTrue(allFriendsInDb.any { it.userId == userId2 && it.friendId == userId1 })
        Assertions.assertTrue(allFriendsInDb.any { it.userId == userId1 && it.friendId == userId2 })

    }

    @Test
    fun `get all friends for a user`() = runBlocking {
        friendRepository.addFriend(userId1, userId2)

        val allFriends = friendRepository.getAllFriends(userId1)
        Assertions.assertEquals(1, allFriends.size)
        Assertions.assertEquals("Watson", allFriends[0].lastName)
    }

    @Test
    fun `delete a friend for a user`() = runBlocking {
        friendRepository.addFriend(userId1, userId2)

        val allFriendsBeforeDelete = friendRepository.getAllFriends(userId1)
        Assertions.assertEquals(1, allFriendsBeforeDelete.size)
        Assertions.assertEquals("Watson", allFriendsBeforeDelete[0].lastName)

        friendRepository.deleteFriend(userId1, userId2)

        val allFriendsAfterDelete = friendRepository.getAllFriends(userId1)
        Assertions.assertEquals(0, allFriendsAfterDelete.size)
    }


    @AfterAll
    fun tearDown() {
        transaction {
            SchemaUtils.drop(Users, Friends, AuthCredentials)
        }
        stopKoin()
    }
}