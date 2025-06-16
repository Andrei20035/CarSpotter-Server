package data.testutils

import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName

object TestDatabase {
    private val dbName = System.getenv("TEST_DB_NAME") ?: error("TEST_DB_NAME not set")
    private val dbUser = System.getenv("TEST_DB_USER") ?: error("TEST_DB_USER not set")
    private val dbPassword = System.getenv("TEST_DB_PASSWORD") ?: error("TEST_DB_PASSWORD not set")

    val postgresContainer: PostgreSQLContainer<Nothing> = PostgreSQLContainer<Nothing>(
        DockerImageName.parse("postgres:latest")
    ).apply {
        withDatabaseName(dbName)
        withUsername(dbUser)
        withPassword(dbPassword)
        start() // Start once for all tests
    }
}