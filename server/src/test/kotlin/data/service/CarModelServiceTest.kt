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
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.getValue
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
    fun `create and get car model by id`() = runBlocking {
        val id = carModelService.createCarModel(
            CarModel(
                brand = "Toyota",
                model = "GR Supra",
                year = 2022
            )
        )

        val result = carModelService.getCarModel(id)

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
        val result = carModelService.getCarModel(id)

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