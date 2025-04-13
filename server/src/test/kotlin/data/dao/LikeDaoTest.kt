package data.dao

import com.carspotter.data.dao.auth_credentials.AuthCredentialDaoImpl
import com.carspotter.data.dao.car_model.CarModelDaoImpl
import com.carspotter.data.dao.like.LikeDaoImpl
import com.carspotter.data.dao.post.PostDaoImpl
import com.carspotter.data.dao.user.UserDaoImpl
import com.carspotter.data.model.AuthCredential
import com.carspotter.data.model.CarModel
import com.carspotter.data.model.Post
import com.carspotter.data.model.User
import com.carspotter.data.table.*
import data.testutils.TestDatabase
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.time.LocalDate

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LikeDaoTest {

    private lateinit var likeDao: LikeDaoImpl
    private lateinit var userDao: UserDaoImpl
    private lateinit var postDao: PostDaoImpl
    private lateinit var carModelDao: CarModelDaoImpl
    private lateinit var authCredentialDao: AuthCredentialDaoImpl

    private var credentialId1: Int = 0
    private var credentialId2: Int = 0
    private var userId1: Int = 0
    private var userId2: Int = 0
    private var postId1: Int = 0
    private var carModelId1: Int = 0

    @BeforeAll
    fun setupDatabase() {
        Database.connect(
            url = TestDatabase.postgresContainer.jdbcUrl,
            driver = "org.postgresql.Driver",
            user = TestDatabase.postgresContainer.username,
            password = TestDatabase.postgresContainer.password
        )

        transaction {
            SchemaUtils.create(Users, Posts, CarModels, Likes, AuthCredentials)
        }

        likeDao = LikeDaoImpl()
        userDao = UserDaoImpl()
        postDao = PostDaoImpl()
        carModelDao = CarModelDaoImpl()
        authCredentialDao = AuthCredentialDaoImpl()

        runBlocking {
            credentialId1 = authCredentialDao.createCredentials(
                AuthCredential(
                    email = "test1@test.com",
                    password = "test1",
                    providerId = "231122",
                    provider = "google"
                )
            )
            credentialId2 = authCredentialDao.createCredentials(
                AuthCredential(
                    email = "test2@test.com",
                    password = "test2",
                    providerId = "2311",
                    provider = "local"
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
            carModelId1 = carModelDao.createCarModel(
                CarModel(
                    brand = "BMW",
                    model = "M3",
                    year = 2020
                )
            )
            postId1 = postDao.createPost(
                Post(
                    userId = userId1,
                    imagePath = "path/to/image1",
                    description = "Description1",
                    carModelId = carModelId1
                )
            )
        }
    }

    @BeforeEach
    fun cleanDatabase() {
        transaction {
            Likes.deleteAll()
        }
    }

    @Test
    fun `like post`() = runBlocking {
        likeDao.likePost(userId1, postId1)

        val usersWhoLike = likeDao.getLikesForPost(postId1)

        assertEquals(1, usersWhoLike.size)
        assertTrue(usersWhoLike.any { it.id == userId1 })
    }

    @Test
    fun `unlikePost should remove the like from the post`() = runBlocking {
        likeDao.likePost(userId1, postId1)
        var usersWhoLike = likeDao.getLikesForPost(postId1)
        assertEquals(1, usersWhoLike.size)

        likeDao.unlikePost(userId1, postId1)
        usersWhoLike = likeDao.getLikesForPost(postId1)
        assertTrue(usersWhoLike.isEmpty())
    }

    @Test
    fun `getLikesForPost should return all users who liked a post`() = runBlocking {
        likeDao.likePost(userId1, postId1)
        likeDao.likePost(userId2, postId1)

        val usersWhoLike = likeDao.getLikesForPost(postId1)

        assertEquals(2, usersWhoLike.size)
        assertTrue(usersWhoLike.any { it.id == userId1 })
        assertTrue(usersWhoLike.any { it.id == userId2 })
    }

    @AfterAll
    fun tearDown() {
        transaction {
            SchemaUtils.drop(Users, Posts, CarModels, Likes, AuthCredentials)
        }
    }
}
