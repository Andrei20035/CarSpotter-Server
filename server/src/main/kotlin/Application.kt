package com.carspotter

import com.carspotter.di.daoModule
import com.carspotter.di.repositoryModule
import com.carspotter.di.serviceModule
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() { // TODO: Put application.yaml data in .env file
    install(Koin) {
        slf4jLogger() // Optional, for logging Koin's behavior
        modules(daoModule, repositoryModule, serviceModule) // Add DI modules here
    }

    install(RoutingRoot)

    val currentEnvironment = environment.config.propertyOrNull("ktor.environment")?.getString() ?: "development"
    log.info("Running in $currentEnvironment environment")

    configureSockets()
    configureSecurity()
    configureHTTP()
    configureSerialization()
    configureDatabases()
    configureMonitoring()
    configureRouting()
}
