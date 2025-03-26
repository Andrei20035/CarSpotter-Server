package com.carspotter

import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {

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
