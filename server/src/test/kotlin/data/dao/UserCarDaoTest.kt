package data.dao

import com.carspotter.data.dao.car_model.CarModelDaoImpl
import com.carspotter.data.dao.user.UserDaoImpl
import com.carspotter.data.dao.user_car.UserCarDaoImpl
import com.carspotter.data.model.CarModel
import com.carspotter.data.model.UserCar
import com.carspotter.data.model.User
import com.carspotter.data.table.Users
import com.carspotter.data.table.UsersCars
import data.testutils.TestDatabase
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.Assertions.*
import java.time.LocalDate

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserCarDaoTest {

    private lateinit var userCarDao: UserCarDaoImpl
    private lateinit var userDao: UserDaoImpl
    private lateinit var carModelDao: CarModelDaoImpl


    private var userId1: Int = 0
    private var userCarId1: Int = 0
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
            SchemaUtils.create(UsersCars, Users)
        }

        userCarDao = UserCarDaoImpl()
        userDao = UserDaoImpl()
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
            carModelId1 = carModelDao.createCarModel(
                CarModel(
                    brand = "BMW",
                    model = "M3",
                    year = 2020
                )
            )
        }
    }

    @BeforeEach
    fun cleanDatabase() {
        transaction {
            UsersCars.deleteAll()
        }
    }

    @Test
    fun `create and retrieve user car by ID`() = runBlocking {
        userCarId1 = userCarDao.createUserCar(
            UserCar(
                userId = userId1,
                carModelId = carModelId1,
                imagePath = "path/to/car/image"
            )
        )
        val userCar = userCarDao.getUserCarById(userCarId1)

        assertNotNull(userCar)
        assertEquals(userCarId1, userCar?.userId)
        assertEquals(carModelId1, userCar?.carModelId)
        assertEquals("path/to/car/image", userCar?.imagePath)
    }

    @Test
    fun `get user car by user ID`() = runBlocking {
        val userCar = userCarDao.getUserCarByUserId(userId1)

        assertNotNull(userCar)
        assertEquals(userId1, userCar?.userId)
        assertEquals(1, userCar?.carModelId)
        assertEquals("path/to/car/image", userCar?.imagePath)
    }

    @Test
    fun `get user by user car ID`() = runBlocking {
        val user = userCarDao.getUserByUserCarId(userCarId1)

        assertNotNull(user)
        assertEquals(userId1, user.id)
        assertEquals("Peter", user.firstName)
        assertEquals("Parker", user.lastName)
    }

    @Test
    fun `update user car`() = runBlocking {
        // Update the user car
        userCarDao.updateUserCar(userId1, "new/path/to/car/image", 2)

        // Verify the update
        val userCar = userCarDao.getUserCarByUserId(userId1)
        assertNotNull(userCar)
        assertEquals("new/path/to/car/image", userCar?.imagePath)
        assertEquals(2, userCar?.carModelId)
    }

    @Test
    fun `delete user car`() = runBlocking {
        // Delete the user car
        userCarDao.deleteUserCar(userId1)

        // Verify the deletion
        val userCar = userCarDao.getUserCarByUserId(userId1)
        assertNull(userCar) // It should be null since the car was deleted
    }

    @Test
    fun `get all user cars`() = runBlocking {
        // Create additional user cars
        userCarDao.createUserCar(
            UserCar(
                userId = 2,
                carModelId = 1,
                imagePath = "path/to/another/car/image"
            )
        )

        val allUserCars = userCarDao.getAllUserCars()

        assertTrue(allUserCars.size >= 1) // We should have at least one user car
    }

    @AfterAll
    fun tearDown() {
        transaction {
            SchemaUtils.drop(UsersCars) // Drop the UsersCars table after tests
        }
    }
}
