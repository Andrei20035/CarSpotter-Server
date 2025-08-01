package data.repository

import com.carspotter.data.model.*
import com.carspotter.data.repository.auth_credential.IAuthCredentialRepository
import com.carspotter.data.repository.car_model.ICarModelRepository
import com.carspotter.data.repository.user.IUserRepository
import com.carspotter.data.repository.user_car.IUserCarRepository
import com.carspotter.data.table.AuthCredentials
import com.carspotter.data.table.CarModels
import com.carspotter.data.table.Users
import com.carspotter.data.table.UsersCars
import com.carspotter.di.daoModule
import com.carspotter.di.repositoryModule
import data.testutils.SchemaSetup
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
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import java.time.LocalDate
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserCarRepositoryTest: KoinTest {

    private val userCarRepository: IUserCarRepository by inject()
    private val userRepository: IUserRepository by inject()
    private val carModelRepository: ICarModelRepository by inject()
    private val authCredentialRepository: IAuthCredentialRepository by inject()

    private var credentialId1: UUID = UUID.randomUUID()
    private var credentialId2: UUID = UUID.randomUUID()
    private var userId1: UUID = UUID.randomUUID()
    private var userId2: UUID = UUID.randomUUID()
    private var userCarId1: UUID = UUID.randomUUID()
    private var carModelId1: UUID = UUID.randomUUID()
    private var carModelId2: UUID = UUID.randomUUID()


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
        SchemaSetup.createUsersCarsTable(UsersCars)
        SchemaSetup.createAuthCredentialsTableWithConstraint(AuthCredentials)
        SchemaSetup.createCarModelsTable(CarModels)

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
                    startYear = 2020,
                    endYear = 2023
                )
            )
            carModelId2 = carModelRepository.createCarModel(
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
        }
    }

    @Test
    fun `create and retrieve user car by ID`() = runBlocking {
        userCarId1 = userCarRepository.createUserCar(
            UserCar(
                userId = userId1,
                carModelId = carModelId1,
                imagePath = "path/to/car/image"
            )
        )
        val userCar = userCarRepository.getUserCarById(userCarId1)

        assertNotNull(userCar)
        assertEquals(userId1, userCar?.userId)
        assertEquals(carModelId1, userCar?.carModelId)
        assertEquals("path/to/car/image", userCar?.imagePath)
    }

    @Test
    fun `get user car by user ID`() = runBlocking {
        userCarId1 = userCarRepository.createUserCar(
            UserCar(
                userId = userId1,
                carModelId = carModelId1,
                imagePath = "path/to/car/image"
            )
        )
        val userCar = userCarRepository.getUserCarByUserId(userId1)

        assertNotNull(userCar)
        assertEquals(userId1, userCar?.userId)
        assertEquals(carModelId1, userCar?.carModelId)
        assertEquals("path/to/car/image", userCar?.imagePath)
    }

    @Test
    fun `get user by user car ID`() = runBlocking {
        userCarId1 = userCarRepository.createUserCar(
            UserCar(
                userId = userId1,
                carModelId = carModelId1,
                imagePath = "path/to/car/image"
            )
        )

        val user = userCarRepository.getUserByUserCarId(userCarId1)

        assertNotNull(user)
        assertEquals(userId1, user.id)
        assertEquals("Peter Parker", user.fullName)
    }

    @Test
    fun `update user car`() = runBlocking {
        userCarRepository.createUserCar(
            UserCar(
                userId = userId1,
                carModelId = carModelId2,
                imagePath = "path/to/car/image"
            )
        )
        // Update only image path
        userCarRepository.updateUserCar(userId1, "new/path/to/car/image", null)

        val userCar = userCarRepository.getUserCarByUserId(userId1)

        assertNotNull(userCar)
        assertEquals("new/path/to/car/image", userCar?.imagePath)
        assertEquals(carModelId2, userCar?.carModelId)

        // Update only the carModelId
        userCarRepository.updateUserCar(userId1, null, carModelId1)

        val updatedUserCar = userCarRepository.getUserCarByUserId(userId1)

        assertNotNull(updatedUserCar)
        assertEquals("new/path/to/car/image", updatedUserCar?.imagePath)
        assertEquals(carModelId1, updatedUserCar?.carModelId)
    }

    @Test
    fun `delete user car`() = runBlocking {
        userCarId1 = userCarRepository.createUserCar(
            UserCar(
                userId = userId1,
                carModelId = carModelId1,
                imagePath = "path/to/car/image"
            )
        )
        var userCars = userCarRepository.getAllUserCars()
        assertTrue(userCars.size == 1)

        // Delete the user car
        userCarRepository.deleteUserCar(userId1)

        // Verify the deletion
        userCars = userCarRepository.getAllUserCars()
        assertTrue(userCars.isEmpty())
    }

    @Test
    fun `get all user cars`() = runBlocking {
        userCarRepository.createUserCar(
            UserCar(
                userId = userId1,
                carModelId = carModelId1,
                imagePath = "path/to/car/image1"
            )
        )
        userCarRepository.createUserCar(
            UserCar(
                userId = userId2,
                carModelId = carModelId2,
                imagePath = "path/to/car/image2"
            )
        )

        val allUserCars = userCarRepository.getAllUserCars()

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
