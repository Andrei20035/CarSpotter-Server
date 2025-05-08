package data.dao

import com.carspotter.data.dao.auth_credentials.IAuthCredentialDAO
import com.carspotter.data.dao.car_model.ICarModelDAO
import com.carspotter.data.dao.post.IPostDAO
import com.carspotter.data.dao.user.IUserDAO
import com.carspotter.data.model.*
import com.carspotter.data.table.AuthCredentials
import com.carspotter.data.table.CarModels
import com.carspotter.data.table.Posts
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
import java.time.ZoneId
import java.time.ZonedDateTime

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PostDaoTest: KoinTest {

    private val userDao: IUserDAO by inject()
    private val postDao: IPostDAO by inject()
    private val carModelDao: ICarModelDAO by inject()
    private val authCredentialDao: IAuthCredentialDAO by inject()

    private var credentialId1: Int = 0
    private var credentialId2: Int = 0
    private var userId1: Int = 0
    private var userId2: Int = 0
    private var carModelId1: Int = 0
    private var carModelId2: Int = 0

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

        SchemaSetup.createUsersTable(Users)
        SchemaSetup.createPostsTable(Posts)
        SchemaSetup.createCarModelsTable(CarModels)
        SchemaSetup.createAuthCredentialsTableWithConstraint(AuthCredentials)

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
            carModelId2 = carModelDao.createCarModel(
                CarModel(
                    brand = "Audi",
                    model = "A4",
                    year = 2021
                )
            )
        }
    }

    @BeforeEach
    fun clearDatabase() {
        transaction {
            Posts.deleteAll()
        }
    }

    @Test
    fun `create and get post by ID`() = runBlocking {
        val postID = postDao.createPost(
            Post(
                userId = userId1,
                imagePath = "path/to/image1",
                description = "Description1",
                carModelId = carModelId1
            )
        )

        val post = postDao.getPostById(postID)

        Assertions.assertNotNull(post)
        Assertions.assertEquals(postID, post?.id)
        Assertions.assertEquals(userId1, post?.userId)
        Assertions.assertEquals("path/to/image1", post?.imagePath)
        Assertions.assertEquals("Description1", post?.description)
        Assertions.assertEquals(carModelId1, post?.carModelId)
    }

    @Test
    fun `get all posts`() = runBlocking {
        postDao.createPost(
            Post(
                userId = userId1,
                imagePath = "path/to/image1",
                description = "Description1",
                carModelId = carModelId1
            )
        )
        postDao.createPost(
            Post(
                userId = userId2,
                imagePath = "path/to/image2",
                description = "Description2",
                carModelId = carModelId2
            )
        )

        val posts = postDao.getAllPosts()

        Assertions.assertEquals(2, posts.size)
        Assertions.assertTrue(posts.any { it.userId == userId1 && it.imagePath == "path/to/image1" && it.description == "Description1" && it.carModelId == carModelId1 })
        Assertions.assertTrue(posts.any { it.userId == userId2 && it.imagePath == "path/to/image2" && it.description == "Description2" && it.carModelId == carModelId2 })
    }

    @Test
    fun `get current day posts`() = runBlocking {
        postDao.createPost(
            Post(
                userId = userId1,
                imagePath = "path/to/image1",
                description = "Description1",
                carModelId = carModelId1
            )
        )
        postDao.createPost(
            Post(
                userId = userId1,
                imagePath = "path/to/image2",
                description = "Description2",
                carModelId = carModelId2
            )
        )

        // Assuming you have a way to get user's time zone, let's use UTC for the example.
        val userTimeZone = ZoneId.of("UTC") // Replace with actual user's time zone if available.

        // Fetch posts for the current day for the user
        val currentDayPosts = postDao.getCurrentDayPostsForUser(userId1, userTimeZone)

        // Assertions
        Assertions.assertNotNull(currentDayPosts)
        Assertions.assertTrue(currentDayPosts.isNotEmpty(), "There should be posts returned for the current day.")

        // Verify the createdAt timestamp falls within today's range
        val now = ZonedDateTime.now(userTimeZone).toLocalDate().atStartOfDay(userTimeZone).toInstant()
        currentDayPosts.forEach { post ->
            // Verify that createdAt is on the current day
            Assertions.assertTrue(post.createdAt!! >= now, "Post createdAt should be after the start of today.")
        }
    }


    @Test
    fun `edit post description`() = runBlocking {
        val postId = postDao.createPost(
            Post(
                userId = userId1,
                imagePath = "path/to/image1",
                description = "Description1",
                carModelId = carModelId1
            )
        )

        val postDescription = postDao.getPostById(postId)?.description
        Assertions.assertEquals("Description1", postDescription)

        postDao.editPost(postId, "New description")

        val newDescription = postDao.getPostById(postId)?.description
        Assertions.assertEquals("New description", newDescription)
    }

    @Test
    fun `delete post`() = runBlocking {
        val postId = postDao.createPost(
            Post(
                userId = userId1,
                imagePath = "path/to/image1",
                description = "Description1",
                carModelId = carModelId1
            )
        )
        postDao.deletePost(postId)
        val deletedPost = postDao.getPostById(postId)

        assertNull(deletedPost)
    }

    @AfterAll
    fun tearDown() {
        transaction {
            SchemaUtils.drop(Posts, Users, CarModels, AuthCredentials)
        }
        stopKoin()
    }
}