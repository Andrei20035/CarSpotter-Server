package data.dao

import com.carspotter.data.dao.auth_credentials.AuthCredentialDaoImpl
import com.carspotter.data.dao.car_model.CarModelDaoImpl
import com.carspotter.data.dao.user.UserDaoImpl
import com.carspotter.data.dao.user_car.UserCarDaoImpl
import com.carspotter.data.model.AuthCredential
import com.carspotter.data.model.CarModel
import com.carspotter.data.model.UserCar
import com.carspotter.data.model.User
import com.carspotter.data.table.AuthCredentials
import com.carspotter.data.table.Users
import com.carspotter.data.table.UsersCars
import data.testutils.TestDatabase
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions
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
    private lateinit var authCredentialDao: AuthCredentialDaoImpl

    private var credentialId1: Int = 0
    private var credentialId2: Int = 0
    private var userId1: Int = 0
    private var userId2: Int = 0
    private var userCarId1: Int = 0
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
            SchemaUtils.create(UsersCars, Users, AuthCredentials)
        }

        userCarDao = UserCarDaoImpl()
        userDao = UserDaoImpl()
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
            carModelId2 = carModelDao.createCarModel(
                CarModel(
                    brand = "Tesla",
                    model = "Model 3",
                    year = 2023
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
        userCarId1 = userCarDao.createUserCar(
            UserCar(
                userId = userId1,
                carModelId = carModelId1,
                imagePath = "path/to/car/image"
            )
        )
        val userCar = userCarDao.getUserCarByUserId(userId1)

        assertNotNull(userCar)
        assertEquals(userId1, userCar?.userId)
        assertEquals(carModelId1, userCar?.carModelId)
        assertEquals("path/to/car/image", userCar?.imagePath)
    }

    @Test
    fun `get user by user car ID`() = runBlocking {
        userCarId1 = userCarDao.createUserCar(
            UserCar(
                userId = userId1,
                carModelId = carModelId1,
                imagePath = "path/to/car/image"
            )
        )

        val user = userCarDao.getUserByUserCarId(userCarId1)

        assertNotNull(user)
        assertEquals(userId1, user.id)
        assertEquals("Peter", user.firstName)
        assertEquals("Parker", user.lastName)
    }

    @Test
    fun `update user car`() = runBlocking {
        userCarDao.createUserCar(
            UserCar(
                userId = userId1,
                carModelId = carModelId1,
                imagePath = "path/to/car/image"
            )
        )
        // Update only image path
        userCarDao.updateUserCar(userId1, "new/path/to/car/image", null)

        var userCar = userCarDao.getUserCarByUserId(userId1)

        assertNotNull(userCar)
        assertEquals("new/path/to/car/image", userCar?.imagePath)
        assertEquals(carModelId1, userCar?.carModelId)

        // Update only the carModelId
        userCarDao.updateUserCar(userId1, null, carModelId2)

        userCar = userCarDao.getUserCarByUserId(userId1)

        assertNotNull(userCar)
        assertEquals("new/path/to/car/image", userCar?.imagePath)
        assertEquals(carModelId2, userCar?.carModelId)
    }

    @Test
    fun `delete user car`() = runBlocking {
        userCarId1 = userCarDao.createUserCar(
            UserCar(
                userId = userId1,
                carModelId = carModelId1,
                imagePath = "path/to/car/image"
            )
        )
        var userCars = userCarDao.getAllUserCars()
        assertTrue(userCars.size == 1)

        // Delete the user car
        userCarDao.deleteUserCar(userId1)

        // Verify the deletion
        userCars = userCarDao.getAllUserCars()
        assertTrue(userCars.isEmpty())
    }

    @Test
    fun `get all user cars`() = runBlocking {
        userCarDao.createUserCar(
            UserCar(
                userId = userId1,
                carModelId = carModelId1,
                imagePath = "path/to/car/image1"
            )
        )
        userCarDao.createUserCar(
            UserCar(
                userId = userId2,
                carModelId = carModelId2,
                imagePath = "path/to/car/image2"
            )
        )

        val allUserCars = userCarDao.getAllUserCars()

        assertTrue(allUserCars.size == 2)
    }

    @AfterAll
    fun tearDown() {
        transaction {
            SchemaUtils.drop(UsersCars, Users, AuthCredentials)
        }
    }
}
