package data.service

import com.carspotter.data.model.*
import com.carspotter.data.service.auth_credential.IAuthCredentialService
import com.carspotter.data.service.car_model.ICarModelService
import com.carspotter.data.service.user.IUserService
import com.carspotter.data.service.user_car.IUserCarService
import com.carspotter.data.table.AuthCredentials
import com.carspotter.data.table.CarModels
import com.carspotter.data.table.Users
import com.carspotter.data.table.UsersCars
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
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import java.time.LocalDate

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserCarServiceTest: KoinTest {

    private val userCarService: IUserCarService by inject()
    private val userService: IUserService by inject()
    private val carModelService: ICarModelService by inject()
    private val authCredentialService: IAuthCredentialService by inject()

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

        startKoin {
            modules(daoModule, repositoryModule, serviceModule)
        }

        SchemaSetup.createUsersTable(Users)
        SchemaSetup.createUsersCarsTable(UsersCars)
        SchemaSetup.createAuthCredentialsTableWithConstraint(AuthCredentials)
        SchemaSetup.createCarModelsTable(CarModels)

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
                    startYear = 2020,
                    endYear = 2023
                )
            )
            carModelId2 = carModelService.createCarModel(
                CarModel(
                    brand = "Tesla",
                    model = "Model 3",
                    startYear = 2022,
                    endYear = 2024,
                )
            )
        }
    }

    @BeforeEach
    fun cleanDatabase() {
        transaction {
            UsersCars.deleteAll()
            exec("ALTER SEQUENCE users_cars_id_seq RESTART WITH 1")
        }
    }

    @Test
    fun `create and retrieve user car by ID`() = runBlocking {
        userCarId1 = userCarService.createUserCar(
            UserCar(
                userId = userId1,
                carModelId = carModelId1,
                imagePath = "path/to/car/image"
            )
        )
        val userCar = userCarService.getUserCarById(userCarId1)

        assertNotNull(userCar)
        assertEquals(userCarId1, userCar?.userId)
        assertEquals(carModelId1, userCar?.carModelId)
        assertEquals("path/to/car/image", userCar?.imagePath)
    }

    @Test
    fun `get user car by user ID`() = runBlocking {
        userCarId1 = userCarService.createUserCar(
            UserCar(
                userId = userId1,
                carModelId = carModelId1,
                imagePath = "path/to/car/image"
            )
        )
        val userCar = userCarService.getUserCarByUserId(userId1)

        assertNotNull(userCar)
        assertEquals(userId1, userCar?.userId)
        assertEquals(carModelId1, userCar?.carModelId)
        assertEquals("path/to/car/image", userCar?.imagePath)
    }

    @Test
    fun `get user by user car ID`() = runBlocking {
        userCarId1 = userCarService.createUserCar(
            UserCar(
                userId = userId1,
                carModelId = carModelId1,
                imagePath = "path/to/car/image"
            )
        )

        val user = userCarService.getUserByUserCarId(userCarId1)

        assertNotNull(user)
        assertEquals(userId1, user.id)
        assertEquals("Peter Parker", user.fullName)
    }

    @Test
    fun `update user car`() = runBlocking {
        userCarService.createUserCar(
            UserCar(
                userId = userId1,
                carModelId = carModelId2,
                imagePath = "path/to/car/image"
            )
        )
        userCarService.updateUserCar(userId1, "new/path/to/car/image", null)

        val userCar = userCarService.getUserCarByUserId(userId1)

        assertNotNull(userCar)
        assertEquals("new/path/to/car/image", userCar?.imagePath)
        assertEquals(carModelId2, userCar?.carModelId)

        userCarService.updateUserCar(userId1, null, carModelId1)

        val updatedUserCar = userCarService.getUserCarByUserId(userId1)

        assertNotNull(updatedUserCar)
        assertEquals("new/path/to/car/image", updatedUserCar?.imagePath)
        assertEquals(carModelId1, updatedUserCar?.carModelId)
    }

    @Test
    fun `delete user car`() = runBlocking {
        userCarId1 = userCarService.createUserCar(
            UserCar(
                userId = userId1,
                carModelId = carModelId1,
                imagePath = "path/to/car/image"
            )
        )
        var userCars = userCarService.getAllUserCars()
        assertTrue(userCars.size == 1)

        // Delete the user car
        userCarService.deleteUserCar(userId1)

        // Verify the deletion
        userCars = userCarService.getAllUserCars()
        assertTrue(userCars.isEmpty())
    }

    @Test
    fun `get all user cars`() = runBlocking {
        userCarService.createUserCar(
            UserCar(
                userId = userId1,
                carModelId = carModelId1,
                imagePath = "path/to/car/image1"
            )
        )
        userCarService.createUserCar(
            UserCar(
                userId = userId2,
                carModelId = carModelId2,
                imagePath = "path/to/car/image2"
            )
        )

        val allUserCars = userCarService.getAllUserCars()

        assertTrue(allUserCars.size == 2)
    }

    @AfterAll
    fun tearDown() {
        transaction {
            SchemaUtils.drop(UsersCars, Users, AuthCredentials)
        }
        stopKoin()
    }
}