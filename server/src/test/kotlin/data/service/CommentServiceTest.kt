package data.service

import com.carspotter.data.model.*
import com.carspotter.data.service.auth_credential.IAuthCredentialService
import com.carspotter.data.service.car_model.ICarModelService
import com.carspotter.data.service.comment.ICommentService
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
class CommentServiceTest: KoinTest {

    private val commentService: ICommentService by inject()
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

        SchemaSetup.createCommentsTable(Comments)
        SchemaSetup.createUsersTable(Users)
        SchemaSetup.createAuthCredentialsTableWithConstraint(AuthCredentials)
        SchemaSetup.createPostsTable(Posts)
        SchemaSetup.createCarModelsTable(CarModels)

        runBlocking {
            credentialId1 = authCredentialService.createCredentials(
                AuthCredential(
                    email = "repo1@test.com",
                    password = null,
                    googleId = "google1",
                    provider = AuthProvider.GOOGLE
                )
            )
            credentialId2 = authCredentialService.createCredentials(
                AuthCredential(
                    email = "repo2@test.com",
                    password = "pass2",
                    googleId = null,
                    provider = AuthProvider.REGULAR
                )
            )
            userId1 = userService.createUser(
                User(
                    authCredentialId = credentialId1,
                    fullName = "Tony Stark",
                    phoneNumber = "0712453678",
                    birthDate = LocalDate.of(1970, 5, 29),
                    username = "IronMan",
                    country = "USA"
                )
            )
            userId2 = userService.createUser(
                User(
                    authCredentialId = credentialId2,
                    fullName = "Steve Rogers",
                    phoneNumber = "0712453678",
                    birthDate = LocalDate.of(1918, 7, 4),
                    username = "Captain",
                    country = "USA"
                )
            )
            carModelId1 = carModelService.createCarModel(
                CarModel(
                    brand = "Audi",
                    model = "R8",
                    year = 2022
                )
            )
            postId1 = postService.createPost(
                Post(
                    userId = userId1,
                    imagePath = "path/to/image_repo",
                    description = "Tony’s car",
                    carModelId = carModelId1
                )
            )
        }
    }

    @BeforeEach
    fun cleanDatabase() {
        transaction {
            Comments.deleteAll()
        }
    }

    @Test
    fun `add comment and retrieve it`() = runBlocking {
        val commentId = commentService.addComment(
            userId = userId1,
            postId = postId1,
            commentText = "Sleek ride!"
        )
        val comments = commentService.getCommentsForPost(postId1)

        assertNotNull(commentId)
        assertEquals(1, comments.size)
        assertEquals("Sleek ride!", comments[0].commentText)
    }

    @Test
    fun `remove comment`() = runBlocking {
        val commentId = commentService.addComment(
            userId = userId2,
            postId = postId1,
            commentText = "Clean shot!"
        )
        val rowsDeleted = commentService.deleteComment(commentId)
        val comments = commentService.getCommentsForPost(postId1)

        assertEquals(1, rowsDeleted)
        assertTrue(comments.isEmpty())
    }

    @Test
    fun `getCommentsForPost should return all comments for a post`() = runBlocking {
        commentService.addComment(
            userId = userId1,
            postId = postId1,
            commentText = "Fast and furious!"
        )
        commentService.addComment(
            userId = userId2,
            postId = postId1,
            commentText = "Classic Tony."
        )

        val comments = commentService.getCommentsForPost(postId1)

        assertEquals(2, comments.size)
        assertTrue(comments.any { it.commentText == "Fast and furious!" })
        assertTrue(comments.any { it.commentText == "Classic Tony." })
    }

    @Test
    fun `get comment by Id`() = runBlocking {
        val commId1 = commentService.addComment(userId1, postId1, "Awesome car!")
        val commId2 = commentService.addComment(userId2, postId1, "Wow, great spot!")

        val comments = commentService.getCommentsForPost(postId1)

        assertEquals(2, comments.size)

        val comm1 = commentService.getCommentById(commId1)
        val comm2 = commentService.getCommentById(commId2)

        Assertions.assertNotNull(comm1)
        Assertions.assertNotNull(comm2)

        assertEquals("Awesome car!", comm1!!.commentText)
        assertEquals("Wow, great spot!", comm2!!.commentText)

    }

    @AfterAll
    fun tearDown() {
        transaction {
            SchemaUtils.drop(Users, Comments, Posts, CarModels, AuthCredentials)
        }
        stopKoin()
    }
}