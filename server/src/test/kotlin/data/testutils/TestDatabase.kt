package data.testutils

import io.github.cdimascio.dotenv.dotenv
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName

object TestDatabase {
    private val dotenv = dotenv()

    private val dbName = dotenv["TEST_DB_NAME"] ?: error("TEST_DB_NAME not set")
    private val dbUser = dotenv["TEST_DB_USER"] ?: error("TEST_DB_USER not set")
    private val dbPassword = dotenv["TEST_DB_PASSWORD"] ?: error("TEST_DB_PASSWORD not set")

    val postgresContainer: PostgreSQLContainer<Nothing> = PostgreSQLContainer<Nothing>(
        DockerImageName.parse("postgres:latest")
    ).apply {
        withDatabaseName(dbName)
        withUsername(dbUser)
        withPassword(dbPassword)
        start() // Start once for all tests
    }
}