package com.carspotter

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database

fun Application.configureDatabases() {
    val databaseUrl = System.getenv("DEV_DB_URL")
    val dbUser = System.getenv("DEV_USER") ?: error("DEV_USER not set")
    val dbPassword = System.getenv("DEV_PASSWORD") ?: error("DEV_PASSWORD not set")

    if(databaseUrl != null) {
        val hikariConfig = HikariConfig().apply {
            jdbcUrl = databaseUrl
            driverClassName = "org.postgresql.Driver"
            username = dbUser
            password = dbPassword
            maximumPoolSize = 10
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }
        val dataSource = HikariDataSource(hikariConfig)
        Database.connect(dataSource)
    } else {
        // Use existing configuration for local development
        val env = environment.config
        val environmentType = env.propertyOrNull("ktor.environment")?.getString() ?: "development"
        val dbConfig = env.config("database.$environmentType")

        val hikariConfig = HikariConfig().apply {
            jdbcUrl = dbConfig.property("url").getString()
            driverClassName = "org.postgresql.Driver"
            username = dbConfig.property("user").getString()
            password = dbConfig.property("password").getString()
            maximumPoolSize = 10
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }

        val dataSource = HikariDataSource(hikariConfig)
        Database.connect(dataSource)
    }

    environment.log.info("Database connected using HikariCP.")
}



