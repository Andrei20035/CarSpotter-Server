package data.repository

import com.carspotter.data.model.*
import com.carspotter.data.repository.auth_credential.IAuthCredentialRepository
import com.carspotter.data.repository.car_model.ICarModelRepository
import com.carspotter.data.repository.post.IPostRepository
import com.carspotter.data.repository.user.IUserRepository
import com.carspotter.data.table.AuthCredentials
import com.carspotter.data.table.CarModels
import com.carspotter.data.table.Posts
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
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PostRepositoryTest: KoinTest {

    private val userRepository: IUserRepository by inject()
    private val postRepository: IPostRepository by inject()
    private val carModelRepository: ICarModelRepository by inject()
    private val authCredentialRepository: IAuthCredentialRepository by inject()

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
            modules(daoModule, repositoryModule)
        }

        SchemaSetup.createUsersTable(Users)
        SchemaSetup.createPostsTable(Posts)
        SchemaSetup.createCarModelsTable(CarModels)
        SchemaSetup.createAuthCredentialsTableWithConstraint(AuthCredentials)

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
            userId1 = userRepository.createUser(
                User(
                    authCredentialId = credentialId1,
                    fullName = "Peter Parker",
                    phoneNumber = "0712453678",
                    birthDate = LocalDate.of(2003, 11, 8),
                    username = "Socate123",
                    country = "USA"
                )
            )
            userId2 = userRepository.createUser(
                User(
                    authCredentialId = credentialId2,
                    fullName = "Peter Parker",
                    phoneNumber = "0712453678",
                    birthDate = LocalDate.of(2004, 4, 1),
                    username = "Socate321",
                    country = "USA"
                )
            )

            carModelId1 = carModelRepository.createCarModel(
                CarModel(
                    brand = "BMW",
                    model = "M3",
                    year = 2020
                )
            )
            carModelId2 = carModelRepository.createCarModel(
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
        val postID = postRepository.createPost(
            Post(
                userId = userId1,
                imagePath = "path/to/image1",
                description = "Description1",
                carModelId = carModelId1
            )
        )

        val post = postRepository.getPostById(postID)

        Assertions.assertNotNull(post)
        Assertions.assertEquals(postID, post?.id)
        Assertions.assertEquals(userId1, post?.userId)
        Assertions.assertEquals("path/to/image1", post?.imagePath)
        Assertions.assertEquals("Description1", post?.description)
        Assertions.assertEquals(carModelId1, post?.carModelId)
    }

    @Test
    fun `get all posts`() = runBlocking {
        postRepository.createPost(
            Post(
                userId = userId1,
                imagePath = "path/to/image1",
                description = "Description1",
                carModelId = carModelId1
            )
        )
        postRepository.createPost(
            Post(
                userId = userId2,
                imagePath = "path/to/image2",
                description = "Description2",
                carModelId = carModelId2
            )
        )

        val posts = postRepository.getAllPosts()

        Assertions.assertEquals(2, posts.size)
        Assertions.assertTrue(posts.any { it.userId == userId1 && it.imagePath == "path/to/image1" && it.description == "Description1" && it.carModelId == carModelId1 })
        Assertions.assertTrue(posts.any { it.userId == userId2 && it.imagePath == "path/to/image2" && it.description == "Description2" && it.carModelId == carModelId2 })
    }

    @Test
    fun `get current day posts`() = runBlocking {
        val userTimeZone = ZoneId.of("UTC")
        val startOfDay = ZonedDateTime.now(userTimeZone).toLocalDate().atStartOfDay(userTimeZone).toInstant()
        val endOfDay = ZonedDateTime.now(userTimeZone).toLocalDate().plusDays(1).atStartOfDay(userTimeZone).toInstant()

        postRepository.createPost(
            Post(
                userId = userId1,
                imagePath = "path/to/image1",
                description = "Description1",
                carModelId = carModelId1
            )
        )
        postRepository.createPost(
            Post(
                userId = userId1,
                imagePath = "path/to/image2",
                description = "Description2",
                carModelId = carModelId2
            )
        )

        val currentDayPosts = postRepository.getCurrentDayPostsForUser(userId1, startOfDay, endOfDay)

        Assertions.assertNotNull(currentDayPosts)
        Assertions.assertTrue(currentDayPosts.isNotEmpty(), "There should be posts returned for the current day.")

        currentDayPosts.forEach { post ->
            Assertions.assertTrue(
                post.createdAt!! >= startOfDay && post.createdAt < endOfDay,
                "Post createdAt should be within the current day range."
            )
        }
    }


    @Test
    fun `edit post description`() = runBlocking {
        val postId = postRepository.createPost(
            Post(
                userId = userId1,
                imagePath = "path/to/image1",
                description = "Description1",
                carModelId = carModelId1
            )
        )

        val postDescription = postRepository.getPostById(postId)?.description
        Assertions.assertEquals("Description1", postDescription)

        postRepository.editPost(postId, "New description")

        val newDescription = postRepository.getPostById(postId)?.description
        Assertions.assertEquals("New description", newDescription)
    }

    @Test
    fun `delete post`() = runBlocking {
        val postId = postRepository.createPost(
            Post(
                userId = userId1,
                imagePath = "path/to/image1",
                description = "Description1",
                carModelId = carModelId1
            )
        )
        postRepository.deletePost(postId)
        val deletedPost = postRepository.getPostById(postId)

        assertNull(deletedPost)
    }

    @AfterAll
    fun tearDown() {
        transaction {
            SchemaUtils.drop(CarModels, Users, Posts, AuthCredentials)
        }
        stopKoin()
    }

}