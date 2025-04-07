package data.dao

import com.carspotter.data.dao.car_model.CarModelDaoImpl
import com.carspotter.data.dao.post.PostDaoImpl
import com.carspotter.data.dao.user.UserDaoImpl
import com.carspotter.data.model.CarModel
import com.carspotter.data.model.Post
import com.carspotter.data.model.User
import com.carspotter.data.table.CarModels
import com.carspotter.data.table.Posts
import com.carspotter.data.table.Users
import data.testutils.TestDatabase
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.javatime.timestamp
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.*
import java.time.LocalDate

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PostDaoTest {

    private lateinit var userDao: UserDaoImpl
    private lateinit var postDao: PostDaoImpl
    private lateinit var carModelDao: CarModelDaoImpl

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

        transaction {
            SchemaUtils.create(Users, Posts, CarModels)
        }

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

        val currentDayPosts = postDao.getCurrentDayPostsForUser(userId1)
        Assertions.assertNotNull(currentDayPosts)
        Assertions.assertTrue(currentDayPosts[0].timestamp)

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
            SchemaUtils.drop(CarModels, Users, Posts)
        }
    }

}