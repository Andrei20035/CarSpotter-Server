package data.service

import com.carspotter.data.model.CarModel
import com.carspotter.data.service.car_model.ICarModelService
import com.carspotter.data.table.CarModels
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
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CarModelServiceTest: KoinTest {

    private val carModelService: ICarModelService by inject()

    @BeforeAll
    fun setup() {
        Database.connect(
            url = TestDatabase.postgresContainer.jdbcUrl,
            driver = "org.postgresql.Driver",
            user = TestDatabase.postgresContainer.username,
            password = TestDatabase.postgresContainer.password
        )

        startKoin {
            modules(daoModule, repositoryModule, serviceModule)
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
    fun `get car models for brand - returns models for specific brand`() = runBlocking {

        carModelService.createCarModel(CarModel(brand = "Toyota", model = "Camry", year = 2021))
        carModelService.createCarModel(CarModel(brand = "Toyota", model = "Corolla", year = 2022))
        carModelService.createCarModel(CarModel(brand = "Toyota", model = "Prius", year = 2023))
        carModelService.createCarModel(CarModel(brand = "Honda", model = "Civic", year = 2020))
        carModelService.createCarModel(CarModel(brand = "Honda", model = "Accord", year = 2021))

        val toyotaModels = carModelService.getCarModelsForBrand("Toyota")
        val hondaModels = carModelService.getCarModelsForBrand("Honda")

        Assertions.assertEquals(3, toyotaModels.size)
        Assertions.assertTrue(toyotaModels.contains("Camry"))
        Assertions.assertTrue(toyotaModels.contains("Corolla"))
        Assertions.assertTrue(toyotaModels.contains("Prius"))

        Assertions.assertEquals(2, hondaModels.size)
        Assertions.assertTrue(hondaModels.contains("Civic"))
        Assertions.assertTrue(hondaModels.contains("Accord"))
    }

    @Test
    fun `getCarModelId returns correct id when brand and model exist`() = runBlocking {
        val carModelId = carModelService.createCarModel(
            CarModel(
                brand = "Tesla",
                model = "Model S",
                year = 2023
            )
        )

        val result = carModelService.getCarModelId("Tesla", "Model S")

        Assertions.assertEquals(carModelId, result)
    }

    @Test
    fun `getCarModelId returns null when brand and model do not exist`() = runBlocking {
        val result = carModelService.getCarModelId("NonExistentBrand", "NonExistentModel")

        org.junit.jupiter.api.assertNull(result)
    }

    @Test
    fun `create and get car model by id`() = runBlocking {
        val id = carModelService.createCarModel(
            CarModel(
                brand = "Toyota",
                model = "GR Supra",
                year = 2022
            )
        )

        val result = carModelService.getCarModelById(id)

        assertNotNull(result)
        assertEquals("Toyota", result?.brand)
        assertEquals("GR Supra", result?.model)
        assertEquals(2022, result?.year)
    }

    @Test
    fun `get all car models returns all items`() = runBlocking {
        carModelService.createCarModel(CarModel(brand = "Lamborghini", model = "Huracan", year = 2021))
        carModelService.createCarModel(CarModel(brand = "Ferrari", model = "296 GTB", year = 2023))

        val allModels = carModelService.getAllCarModels()

        assertEquals(2, allModels.size)
        assertTrue(allModels.any { it.brand == "Lamborghini" && it.model == "Huracan" })
        assertTrue(allModels.any { it.brand == "Ferrari" && it.model == "296 GTB" })
    }

    @Test
    fun `delete car model removes it from database`() = runBlocking {
        val id = carModelService.createCarModel(
            CarModel(brand = "Porsche", model = "911 GT3", year = 2020)
        )

        val deletedCount = carModelService.deleteCarModel(id)
        val result = carModelService.getCarModelById(id)

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