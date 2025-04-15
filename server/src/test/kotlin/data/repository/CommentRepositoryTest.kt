package data.repo

import com.carspotter.data.dao.auth_credential.AuthCredentialDaoImpl
import com.carspotter.data.dao.car_model.CarModelDaoImpl
import com.carspotter.data.dao.comment.CommentDaoImpl
import com.carspotter.data.dao.post.PostDaoImpl
import com.carspotter.data.dao.user.UserDaoImpl
import com.carspotter.data.model.*
import com.carspotter.data.repository.auth_credentials.AuthCredentialRepositoryImpl
import com.carspotter.data.repository.car_model.CarModelRepositoryImpl
import com.carspotter.data.repository.comment.CommentRepositoryImpl
import com.carspotter.data.repository.post.PostRepositoryImpl
import com.carspotter.data.repository.user.UserRepositoryImpl
import com.carspotter.data.table.*
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
class CommentRepositoryTest {

    private lateinit var commentDao: CommentDaoImpl
    private lateinit var commentRepository: CommentRepositoryImpl
    private lateinit var userDao: UserDaoImpl
    private lateinit var userRepository: UserRepositoryImpl
    private lateinit var postDao: PostDaoImpl
    private lateinit var postRepository: PostRepositoryImpl
    private lateinit var carModelDao: CarModelDaoImpl
    private lateinit var carModelRepository: CarModelRepositoryImpl
    private lateinit var authCredentialDao: AuthCredentialDaoImpl
    private lateinit var authCredentialRepository: AuthCredentialRepositoryImpl

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
            SchemaUtils.create(Comments, Users, AuthCredentials, Posts, CarModels)
        }

        commentDao = CommentDaoImpl()
        commentRepository = CommentRepositoryImpl(commentDao)
        userDao = UserDaoImpl()
        userRepository = UserRepositoryImpl(userDao)
        postDao = PostDaoImpl()
        postRepository = PostRepositoryImpl(postDao)
        carModelDao = CarModelDaoImpl()
        carModelRepository = CarModelRepositoryImpl(carModelDao)
        authCredentialDao = AuthCredentialDaoImpl()
        authCredentialRepository = AuthCredentialRepositoryImpl(authCredentialDao)

        runBlocking {
            credentialId1 = authCredentialRepository.createCredentials(
                AuthCredential(
                    email = "repo1@test.com",
                    password = "pass1",
                    googleId = "google1",
                    provider = AuthProvider.GOOGLE
                )
            )
            credentialId2 = authCredentialRepository.createCredentials(
                AuthCredential(
                    email = "repo2@test.com",
                    password = "pass2",
                    googleId = "google2",
                    provider = AuthProvider.REGULAR
                )
            )
            userId1 = userRepository.createUser(
                User(
                    authCredentialId = credentialId1,
                    firstName = "Tony",
                    lastName = "Stark",
                    birthDate = LocalDate.of(1970, 5, 29),
                    username = "IronMan",
                    country = "USA"
                )
            )
            userId2 = userRepository.createUser(
                User(
                    authCredentialId = credentialId2,
                    firstName = "Steve",
                    lastName = "Rogers",
                    birthDate = LocalDate.of(1918, 7, 4),
                    username = "Captain",
                    country = "USA"
                )
            )
            carModelId1 = carModelRepository.createCarModel(
                CarModel(
                    brand = "Audi",
                    model = "R8",
                    year = 2022
                )
            )
            postId1 = postRepository.createPost(
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
        val commentId = commentRepository.addComment(
            userId = userId1,
            postId = postId1,
            commentText = "Sleek ride!"
        )
        val comments = commentRepository.getCommentsForPost(postId1)

        assertNotNull(commentId)
        assertEquals(1, comments.size)
        assertEquals("Sleek ride!", comments[0].commentText)
    }

    @Test
    fun `remove comment`() = runBlocking {
        val commentId = commentRepository.addComment(
            userId = userId2,
            postId = postId1,
            commentText = "Clean shot!"
        )
        val rowsDeleted = commentRepository.removeComment(commentId)
        val comments = commentRepository.getCommentsForPost(postId1)

        assertEquals(1, rowsDeleted)
        assertTrue(comments.isEmpty())
    }

    @Test
    fun `getCommentsForPost should return all comments for a post`() = runBlocking {
        commentRepository.addComment(
            userId = userId1,
            postId = postId1,
            commentText = "Fast and furious!"
        )
        commentRepository.addComment(
            userId = userId2,
            postId = postId1,
            commentText = "Classic Tony."
        )

        val comments = commentRepository.getCommentsForPost(postId1)

        assertEquals(2, comments.size)
        assertTrue(comments.any { it.commentText == "Fast and furious!" })
        assertTrue(comments.any { it.commentText == "Classic Tony." })
    }

    @AfterAll
    fun tearDown() {
        transaction {
            SchemaUtils.drop(Users, Comments, Posts, CarModels, AuthCredentials)
        }
    }
}
