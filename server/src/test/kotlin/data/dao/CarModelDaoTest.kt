import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName
import java.nio.file.Files
import java.nio.file.Paths
import java.sql.Connection
import java.sql.DriverManager

@Testcontainers
class CarModelDaoImplTest {

    @Container
    private val postgresContainer = PostgreSQLContainer<Nothing>(DockerImageName.parse("postgres:latest")).apply {
        withDatabaseName("carspotter")
        withUsername("postgres")
        withPassword("Andrei2003.")
    }

    private lateinit var connection: Connection

    @BeforeEach
    fun setUp() {
        // Start the PostgreSQL container
        postgresContainer.start()

        // Connect to the database
        connection = DriverManager.getConnection(
            postgresContainer.jdbcUrl,
            postgresContainer.username,
            postgresContainer.password
        )

        val sqlFilePath = "src/test/resources/dbConfig.sql"
        val sql = String(Files.readAllBytes(Paths.get(sqlFilePath)))
        connection.createStatement().execute(sql)

        // Insert some hardcoded data for testing
        val statement = connection.createStatement()
        statement.executeUpdate(
            "INSERT INTO car_models (brand, model, year) VALUES ('BMW', 'M3', 2020), ('Audi', 'A4', 2021)"
        )
    }

    @Test
    fun `verify hardcoded data is inserted correctly`() {
        // Query the database to check if the data was inserted
        val statement = connection.createStatement()
        val resultSet = statement.executeQuery("SELECT * FROM car_models WHERE brand IN ('BMW', 'Audi')")

        val carModels = mutableListOf<String>()
        while (resultSet.next()) {
            val brand = resultSet.getString("brand")
            val model = resultSet.getString("model")
            val year = resultSet.getInt("year")
            carModels.add("$brand $model ($year)")
        }

        // Verify that the hardcoded data is correct
        assertTrue(carModels.contains("BMW M3 (2020)"))
        assertTrue(carModels.contains("Audi A4 (2021)"))
    }

    @AfterEach
    fun tearDown() {
        // Clean up by stopping the container after tests
        connection.close()
        postgresContainer.stop()
    }
}
