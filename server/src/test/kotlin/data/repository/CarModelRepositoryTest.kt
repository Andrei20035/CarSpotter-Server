package data.repository

import com.carspotter.data.model.CarModel
import com.carspotter.data.repository.car_model.ICarModelRepository
import com.carspotter.data.table.CarModels
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
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CarModelRepositoryTest: KoinTest {

    private val carModelRepository: ICarModelRepository by inject()

    @BeforeAll
    fun setup() {
        Database.connect(
            url = TestDatabase.postgresContainer.jdbcUrl,
            driver = "org.postgresql.Driver",
            user = TestDatabase.postgresContainer.username,
            password = TestDatabase.postgresContainer.password
        )

        startKoin {
            modules(daoModule, repositoryModule)
        }

        SchemaSetup.createCarModelsTable(CarModels)
    }

    @BeforeEach
    fun clearDatabase() {
        transaction {
            CarModels.deleteAll()
        }
    }

    @Test
    fun `create and get car model by id`() = runBlocking {
        val id = carModelRepository.createCarModel(
            CarModel(
                brand = "Toyota",
                model = "GR Supra",
                year = 2022
            )
        )

        val result = carModelRepository.getCarModel(id)

        assertNotNull(result)
        assertEquals("Toyota", result?.brand)
        assertEquals("GR Supra", result?.model)
        assertEquals(2022, result?.year)
    }

    @Test
    fun `get all car models returns all items`() = runBlocking {
        carModelRepository.createCarModel(CarModel(brand = "Lamborghini", model = "Huracan", year = 2021))
        carModelRepository.createCarModel(CarModel(brand = "Ferrari", model = "296 GTB", year = 2023))

        val allModels = carModelRepository.getAllCarModels()

        assertEquals(2, allModels.size)
        assertTrue(allModels.any { it.brand == "Lamborghini" && it.model == "Huracan" })
        assertTrue(allModels.any { it.brand == "Ferrari" && it.model == "296 GTB" })
    }

    @Test
    fun `delete car model removes it from database`() = runBlocking {
        val id = carModelRepository.createCarModel(
            CarModel(brand = "Porsche", model = "911 GT3", year = 2020)
        )

        val deletedCount = carModelRepository.deleteCarModel(id)
        val result = carModelRepository.getCarModel(id)

        assertEquals(1, deletedCount)
        assertNull(result)
    }

    @AfterAll
    fun tearDown() {
        transaction {
            SchemaUtils.drop(CarModels)
        }
        stopKoin()
    }
}
