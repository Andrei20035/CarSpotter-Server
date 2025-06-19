package data.service

import com.carspotter.data.model.*
import com.carspotter.data.service.auth_credential.IAuthCredentialService
import com.carspotter.data.service.car_model.ICarModelService
import com.carspotter.data.service.like.ILikeService
import com.carspotter.data.service.post.IPostService
import com.carspotter.data.service.user.IUserService
import com.carspotter.data.table.*
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
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import java.time.LocalDate

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LikeServiceTest: KoinTest {

    private val likeService: ILikeService by inject()
    private val userService: IUserService by inject()
    private val postService: IPostService by inject()
    private val carModelService: ICarModelService by inject()
    private val authCredentialService: IAuthCredentialService by inject()

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

        startKoin {
            modules(daoModule, repositoryModule, serviceModule)
        }

        SchemaSetup.createUsersTable(Users)
        SchemaSetup.createPostsTable(Posts)
        SchemaSetup.createCarModelsTable(CarModels)
        SchemaSetup.createLikesTable(Likes)
        SchemaSetup.createAuthCredentialsTableWithConstraint(AuthCredentials)

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
            userId1 = userService.createUser(
                User(
                    authCredentialId = credentialId1,
                    fullName = "Peter Parker",
                    phoneNumber = "0712453678",
                    birthDate = LocalDate.of(2003, 11, 8),
                    username = "Socate123",
                    country = "USA"
                )
            )
            userId2 = userService.createUser(
                User(
                    authCredentialId = credentialId2,
                    fullName = "Mary Jane",
                    phoneNumber = "0712453678",
                    birthDate = LocalDate.of(2004, 4, 1),
                    username = "Socate321",
                    country = "USA"
                )
            )
            carModelId1 = carModelService.createCarModel(
                CarModel(
                    brand = "BMW",
                    model = "M3",
                    year = 2020
                )
            )
            postId1 = postService.createPost(
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
        likeService.likePost(userId1, postId1)

        val usersWhoLike = likeService.getLikesForPost(postId1)

        assertEquals(1, usersWhoLike.size)
        assertTrue(usersWhoLike.any { it.id == userId1 })
    }

    @Test
    fun `unlikePost should remove the like from the post`() = runBlocking {
        likeService.likePost(userId1, postId1)
        var usersWhoLike = likeService.getLikesForPost(postId1)
        assertEquals(1, usersWhoLike.size)

        likeService.unlikePost(userId1, postId1)
        usersWhoLike = likeService.getLikesForPost(postId1)
        assertTrue(usersWhoLike.isEmpty())
    }

    @Test
    fun `getLikesForPost should return all users who liked a post`() = runBlocking {
        likeService.likePost(userId1, postId1)
        likeService.likePost(userId2, postId1)

        val usersWhoLike = likeService.getLikesForPost(postId1)

        assertEquals(2, usersWhoLike.size)
        assertTrue(usersWhoLike.any { it.id == userId1 })
        assertTrue(usersWhoLike.any { it.id == userId2 })
    }

    @AfterAll
    fun tearDown() {
        transaction {
            SchemaUtils.drop(Users, Posts, CarModels, Likes, AuthCredentials)
        }
        stopKoin()
    }
}