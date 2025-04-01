package data.dao;

import com.carspotter.data.dao.car_model.CarModelDaoImpl
import com.carspotter.data.dao.comment.CommentDaoImpl
import com.carspotter.data.dao.post.PostDaoImpl
import com.carspotter.data.dao.user.UserDaoImpl
import com.carspotter.data.model.CarModel
import com.carspotter.data.model.Post
import com.carspotter.data.model.User
import com.carspotter.data.table.CarModels
import com.carspotter.data.table.Comments
import com.carspotter.data.table.Posts
import com.carspotter.data.table.Users
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
class CommentDaoTest {

    private lateinit var commentDao: CommentDaoImpl
    private lateinit var userDao: UserDaoImpl
    private lateinit var postDao: PostDaoImpl
    private lateinit var carModelDao: CarModelDaoImpl

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
            SchemaUtils.create(Comments, Users, Posts)
        }

        commentDao = CommentDaoImpl()
        userDao = UserDaoImpl()
        postDao = PostDaoImpl()
        carModelDao = CarModelDaoImpl()

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
            Comments.deleteAll()
        }
    }

    @Test
    fun `add comment and retrieve it`() = runBlocking {
        val commentId = commentDao.addComment(userId1, postId1, "Nice car!")
        val comments = commentDao.getCommentsForPost(postId1)

        assertNotNull(commentId)
        assertEquals(1, comments.size)
        assertEquals("Nice car!", comments[0].commentText)
    }

    @Test
    fun `remove comment`() = runBlocking {
        val commentId = commentDao.addComment(userId1, postId1, "Great post!")

        val rowsDeleted = commentDao.removeComment(commentId)
        val comments = commentDao.getCommentsForPost(postId1)

        assertEquals(1, rowsDeleted)
        assertTrue(comments.isEmpty())
    }

    @Test
    fun `getCommentsForPost should return all comments for a post`() = runBlocking {

        commentDao.addComment(userId1, postId1, "Awesome car!")
        commentDao.addComment(userId2, postId1, "Wow, great spot!")

        val comments = commentDao.getCommentsForPost(postId1)

        assertEquals(2, comments.size)
        assertTrue(comments.any { it.commentText == "Awesome car!" })
        assertTrue(comments.any { it.commentText == "Wow, great spot!" })
    }

    @AfterAll
    fun tearDown() {
        transaction {
            SchemaUtils.drop(Users, Comments, Posts, CarModels)
        }
    }


}