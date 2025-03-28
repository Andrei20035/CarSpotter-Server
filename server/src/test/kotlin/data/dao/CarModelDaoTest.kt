import com.carspotter.data.dao.car_model.CarModelDaoImpl
import com.carspotter.data.model.CarModel
import com.carspotter.data.table.CarModels
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.*
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

@Testcontainers
class CarModelDaoImplTest {

    @Container
    private val postgresContainer = PostgreSQLContainer<Nothing>(DockerImageName.parse("postgres:latest")).apply {
        withDatabaseName("carspotter")
        withUsername("postgres")
        withPassword("Andrei2003.")
    }

    private lateinit var carModelDao: CarModelDaoImpl

    @BeforeEach
    fun setUp() {
        postgresContainer.start()

        // Connect Exposed ORM to the TestContainers PostgreSQL instance
        Database.connect(
            url = postgresContainer.jdbcUrl,
            driver = "org.postgresql.Driver",
            user = postgresContainer.username,
            password = postgresContainer.password
        )

        transaction {
            SchemaUtils.create(CarModels) // Ensure the table exists
        }

        carModelDao = CarModelDaoImpl()
    }

    @Test
    fun `create and retrieve a car model`() = runBlocking {
        // Insert a car model
        val carModelId = carModelDao.createCarModel(CarModel(id = 0,"Tesla", "Model S", 2023))

        // Retrieve it
        val retrievedCarModel = carModelDao.getCarModel(carModelId)

        println(retrievedCarModel)

        // Verify
        assertNotNull(retrievedCarModel)
        Assertions.assertEquals("Tesla", retrievedCarModel.brand)
        Assertions.assertEquals("Model S", retrievedCarModel.model)
        Assertions.assertEquals(2023, retrievedCarModel.year)
    }

    @Test
    fun `get all car models`() = runBlocking {
        // Insert car models
        carModelDao.createCarModel(CarModel(0, "BMW", "M3", 2020))
        carModelDao.createCarModel(CarModel(0, "Audi", "A4", 2021))

        // Retrieve all
        val carModels = carModelDao.getAllCarModels()

        // Verify
        Assertions.assertEquals(2, carModels.size)
        Assertions.assertTrue(carModels.any { it.brand == "BMW" && it.model == "M3" && it.year == 2020 })
        Assertions.assertTrue(carModels.any { it.brand == "Audi" && it.model == "A4" && it.year == 2021 })
    }

    @Test
    fun `delete a car model`() = runBlocking {
        // Insert a car model
        val carModelId = carModelDao.createCarModel(CarModel(0, "Mercedes", "C-Class", 2019))

        // Delete it
        carModelDao.deleteCarModel(carModelId)

        // Try to retrieve it
        val deletedCarModel = carModelDao.getCarModel(carModelId)

        // Verify it's gone
        assertNull(deletedCarModel)
    }

    @AfterEach
    fun tearDown() {
        transaction {
            SchemaUtils.drop(CarModels) // Clean up the database
        }
        postgresContainer.stop()
    }
}
