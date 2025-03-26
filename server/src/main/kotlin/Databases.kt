package com.carspotter

import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database

fun Application.configureDatabases() {
    val environmentType = environment.config.propertyOrNull("ktor.environment")?.getString() ?: "development"
    val dbConfig = environment.config.config("database.$environmentType")

    try {
        val database = Database.connect(
            url = dbConfig.property("url").getString(),
            driver = "org.postgresql.Driver",
            user = dbConfig.property("user").getString(),
            password = dbConfig.property("password").getString()
        )
        println("Database connected successfully.")
    } catch (e: Exception) {
        println("Error connecting to the database: ${e.message}")
    }
}



