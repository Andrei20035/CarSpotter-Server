package com.carspotter

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database

fun Application.configureDatabases() {
    val ktorEnv = System.getenv("KTOR_ENV") ?: "development"

    val (databaseUrl, dbUser, dbPassword) = when (ktorEnv) {
        "production" -> {
            val url = System.getenv("DATABASE_URL") ?: error("DATABASE_URL not set")
            val user = System.getenv("POSTGRES_USER") ?: error("POSTGRES_USER not set")
            val password = System.getenv("POSTGRES_PASSWORD") ?: error("POSTGRES_PASSWORD not set")
            Triple(url, user, password)
        }
        "development" -> {
            val url = System.getenv("DEV_DB_URL") ?: error("DEV_DB_URL not set")
            val user = System.getenv("DEV_USER") ?: error("DEV_USER not set")
            val password = System.getenv("DEV_PASSWORD") ?: error("DEV_PASSWORD not set")
            Triple(url, user, password)
        }
        "testing" -> {
            val url = System.getenv("TEST_DB_URL") ?: error("TEST_DB_URL not set")
            val user = System.getenv("TEST_DB_USER") ?: error("TEST_DB_USER not set")
            val password = System.getenv("TEST_DB_PASSWORD") ?: error("TEST_DB_PASSWORD not set")
            Triple(url, user, password)
        }
        else -> error("Unknown KTOR_ENV: $ktorEnv")
    }

    // Make sure DATABASE_URL is JDBC format, if not transform it here
    val jdbcURL = if (!databaseUrl.startsWith("jdbc:")) {
        databaseUrl.replaceFirst("postgresql://", "jdbc:postgresql://")
            .replace(Regex("^(jdbc:postgresql://)([^:/@]+):([^@]+)@"), "$1")
    } else {
        databaseUrl
    }

    val hikariConfig = HikariConfig().apply {
        jdbcUrl =  jdbcURL
        driverClassName = "org.postgresql.Driver"
        username = dbUser
        password = dbPassword
        maximumPoolSize = 10
        isAutoCommit = false
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        validate()
    }
    val dataSource = HikariDataSource(hikariConfig)

    val flyway = Flyway.configure()
        .dataSource(dataSource)
        .locations("classpath:db/migration")
        .load()
    flyway.migrate()

    Database.connect(dataSource)

    environment.log.info("Database connected and migrations applied using Flyway")

}



