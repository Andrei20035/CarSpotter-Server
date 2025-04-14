package data.dao

import com.carspotter.data.dao.auth_credential.AuthCredentialDaoImpl
import com.carspotter.data.dao.friend.FriendDaoImpl
import com.carspotter.data.dao.friend_request.FriendRequestDaoImpl
import com.carspotter.data.dao.user.UserDaoImpl
import com.carspotter.data.model.AuthCredential
import com.carspotter.data.model.AuthProvider
import com.carspotter.data.model.User
import com.carspotter.data.table.AuthCredentials
import com.carspotter.data.table.FriendRequests
import com.carspotter.data.table.Friends
import com.carspotter.data.table.Users
import data.testutils.TestDatabase
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import java.time.LocalDate

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FriendRequestDaoTest {

    private lateinit var userDao: UserDaoImpl
    private lateinit var friendDao: FriendDaoImpl
    private lateinit var friendRequestDao: FriendRequestDaoImpl
    private lateinit var authCredentialDao: AuthCredentialDaoImpl

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

        transaction {
            SchemaUtils.create(Users, AuthCredentials, Friends, FriendRequests)
        }

        userDao = UserDaoImpl()
        friendDao = FriendDaoImpl()
        friendRequestDao = FriendRequestDaoImpl()
        authCredentialDao = AuthCredentialDaoImpl()

        runBlocking {
            credentialId1 = authCredentialDao.createCredentials(
                AuthCredential(
                    email = "test1@test.com",
                    password = "test1",
                    googleId = "231122",
                    provider = AuthProvider.GOOGLE
                )
            )
            credentialId2 = authCredentialDao.createCredentials(
                AuthCredential(
                    email = "test2@test.com",
                    password = "test2",
                    googleId = "2311",
                    provider = AuthProvider.REGULAR
                )
            )
            userId1 = userDao.createUser(
                User(
                    authCredentialId = credentialId1,
                    firstName = "Peter",
                    lastName = "Parker",
                    birthDate = LocalDate.of(2003, 11, 8),
                    username = "Socate123",
                    country = "USA"
                )
            )
            userId2 = userDao.createUser(
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
    fun cleanDatabase() {
        transaction {
            FriendRequests.deleteAll()
        }
    }

    @Test
    fun `sendFriendRequest should add a request to database`() = runBlocking {
        // Act
        val senderId = userId1
        val receiverId = userId2
        friendRequestDao.sendFriendRequest(senderId, receiverId)

        // Assert
        val friendRequests = friendRequestDao.getAllFriendReqFromDB()
        assertEquals(1, friendRequests.size)
    }

    @Test
    fun `acceptFriendRequest should create friendship and delete request`() = runBlocking {

        // Send friend request
        friendRequestDao.sendFriendRequest(userId1, userId2)

        // Verify the request was sent
        var allFriendReq = friendRequestDao.getAllFriendReqFromDB()
        assertEquals(1, allFriendReq.size)

        // Accept friend request
        friendRequestDao.acceptFriendRequest(userId1, userId2)

        // Verify the request was accepted
        allFriendReq = friendRequestDao.getAllFriendReqFromDB()
        assertEquals(0, allFriendReq.size)

        val friendsForUser1 = friendDao.getAllFriends(userId1)
        val friendsForUser2 = friendDao.getAllFriends(userId2)

        assertEquals("Watson", friendsForUser1[0].lastName)
        assertEquals("Parker", friendsForUser2[0].lastName)
    }

    @Test
    fun `declineFriendRequest should remove request but not add friendship`() = runBlocking {
        // Send friend request
        friendRequestDao.sendFriendRequest(userId1, userId2)

        // Verify the request was sent
        var allFriendReq = friendRequestDao.getAllFriendReqFromDB()
        assertEquals(1, allFriendReq.size)

        // Decline friend request
        friendRequestDao.declineFriendRequest(userId1, userId2)

        // Verify the request was declined and friendship was not added
        allFriendReq = friendRequestDao.getAllFriendReqFromDB()
        assertEquals(0, allFriendReq.size)

        val friendsForUser1 = friendDao.getAllFriends(userId1)
        val friendsForUser2 = friendDao.getAllFriends(userId2)

        assertTrue(friendsForUser1.isEmpty())
        assertTrue(friendsForUser2.isEmpty())
    }

    @Test
    fun `getAllFriendRequests should return all pending requests for a user`() = runBlocking {
        friendRequestDao.sendFriendRequest(userId1, userId2)

        val requests = friendRequestDao.getAllFriendRequests(userId2)

        assertEquals(1, requests.size, "User should have one pending friend request")
        assertEquals(userId1, requests.first().id, "Request should be from userId1")
    }

    @AfterAll
    fun tearDown() {
        transaction {
            SchemaUtils.drop(Users, Friends, FriendRequests, AuthCredentials)
        }
    }
}