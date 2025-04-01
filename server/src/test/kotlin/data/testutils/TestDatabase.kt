package data.testutils

import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName

object TestDatabase {
    val postgresContainer: PostgreSQLContainer<Nothing> = PostgreSQLContainer<Nothing>(
        DockerImageName.parse("postgres:latest")
    ).apply {
        withDatabaseName("carspotter")
        withUsername("postgres")
        withPassword("Andrei2003.")
        start() // Start once for all tests
    }
}