package data.dao

import com.carspotter.data.dao.friend.FriendDaoImpl
import com.carspotter.data.dao.user.UserDaoImpl
import com.carspotter.data.model.User
import com.carspotter.data.table.Friends
import com.carspotter.data.table.Users
import data.testutils.TestDatabase
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.*
import java.time.LocalDate

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FriendDaoTest {

    private lateinit var userDao: UserDaoImpl
    private lateinit var friendDao: FriendDaoImpl

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
            SchemaUtils.create(Users, Friends)
        }

        userDao = UserDaoImpl()
        friendDao = FriendDaoImpl()

        runBlocking {
            userId1 = userDao.createUser(
                User(
                    firstName = "Peter",
                    lastName = "Parker",
                    birthDate = LocalDate.of(2003, 11, 8),
                    username = "Socate123",
                    password = "VALIbRAT1",
                    country = "USA"
                )
            )
            userId2 = userDao.createUser(
                User(
                    firstName = "Mary Jane",
                    lastName = "Watson",
                    birthDate = LocalDate.of(2004, 4, 1),
                    username = "Socate321",
                    password = "VALIbRAT2",
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
        val friendId = friendDao.addFriend(userId1, userId2)

        Assertions.assertNotNull(friendId)

        val allFriendsInDb = friendDao.getAllFriendsInDb()

        Assertions.assertEquals(2, allFriendsInDb.size)
        Assertions.assertTrue(allFriendsInDb.any { it.userId == userId2 && it.friendId == userId1 })
        Assertions.assertTrue(allFriendsInDb.any { it.userId == userId1 && it.friendId == userId2 })

    }

    @Test
    fun `get all friends for a user`() = runBlocking {
        friendDao.addFriend(userId1, userId2)

        val allFriends = friendDao.getAllFriends(userId1)
        Assertions.assertEquals(1, allFriends.size)
        Assertions.assertEquals("Watson", allFriends[0].lastName)
    }

    @Test
    fun `delete a friend for a user`() = runBlocking {
        friendDao.addFriend(userId1, userId2)

        val allFriendsBeforeDelete = friendDao.getAllFriends(userId1)
        Assertions.assertEquals(1, allFriendsBeforeDelete.size)
        Assertions.assertEquals("Watson", allFriendsBeforeDelete[0].lastName)

        friendDao.deleteFriend(userId1, userId2)

        val allFriendsAfterDelete = friendDao.getAllFriends(userId1)
        Assertions.assertEquals(0, allFriendsAfterDelete.size)
    }


    @AfterAll
    fun tearDown() {
        transaction {
            SchemaUtils.drop(Users, Friends)
        }
    }
}