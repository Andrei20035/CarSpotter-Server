import com.carspotter.data.dao.car_model.ICarModelDAO
import com.carspotter.data.model.CarModel
import com.carspotter.data.table.CarModels
import com.carspotter.di.daoModule
import data.testutils.SchemaSetup
import data.testutils.TestDatabase
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.*
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CarModelDaoTest: KoinTest {

    private val carModelDao: ICarModelDAO by inject()

    @BeforeAll
    fun setupDatabase() {
        Database.connect(
            url = TestDatabase.postgresContainer.jdbcUrl,
            driver = "org.postgresql.Driver",
            user = TestDatabase.postgresContainer.username,
            password = TestDatabase.postgresContainer.password
        )

        startKoin {
            modules(daoModule)
        }

        SchemaSetup.createCarModelsTable(CarModels)

    }

    @BeforeEach
    fun cleanDatabase() {
        transaction {
            CarModels.deleteAll()
        }
    }

    @Test
    fun `create and retrieve a car model`() = runBlocking {
        val carModelId = carModelDao.createCarModel(
            CarModel(
                brand = "Tesla",
                model = "Model S",
                year = 2023
            )
        )
        val retrievedCarModel = carModelDao.getCarModel(carModelId)

        assertNotNull(retrievedCarModel)
        Assertions.assertEquals("Tesla", retrievedCarModel.brand)
        Assertions.assertEquals("Model S", retrievedCarModel.model)
        Assertions.assertEquals(2023, retrievedCarModel.year)
    }

    @Test
    fun `get all car models`() = runBlocking {
        carModelDao.createCarModel(
            CarModel(
                brand = "BMW",
                model = "M3",
                year = 2020
            )
        )
        carModelDao.createCarModel(
            CarModel(
                brand = "Audi",
                model = "A4",
                year = 2021
            )
        )

        val carModels = carModelDao.getAllCarModels()

        Assertions.assertEquals(2, carModels.size)
        Assertions.assertTrue(carModels.any { it.brand == "BMW" && it.model == "M3" && it.year == 2020 })
        Assertions.assertTrue(carModels.any { it.brand == "Audi" && it.model == "A4" && it.year == 2021 })
    }

    @Test
    fun `delete a car model`() = runBlocking {
        val carModelId = carModelDao.createCarModel(
            CarModel(
                brand = "Mercedes",
                model = "C-Class",
                year = 2019
            )
        )

        carModelDao.deleteCarModel(carModelId)
        val deletedCarModel = carModelDao.getCarModel(carModelId)

        assertNull(deletedCarModel)
    }

    @AfterAll
    fun tearDown() {
        transaction {
            SchemaUtils.drop(CarModels)
        }
        stopKoin()
    }
}
