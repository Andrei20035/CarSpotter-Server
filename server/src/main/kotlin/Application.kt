package com.carspotter

import com.carspotter.di.appModule
import com.carspotter.di.daoModule
import com.carspotter.di.repositoryModule
import com.carspotter.di.serviceModule
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.openapi.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.routing.*
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun main(args: Array<String>) {

    // Print all environment variables
    println("=== ENVIRONMENT VARIABLES ===")
    System.getenv().forEach { (key, value) ->
        println("$key=$value")
    }
    EngineMain.main(args)
}

fun Application.module() {
    install(Koin) {
        slf4jLogger()
        modules(daoModule, repositoryModule, serviceModule, appModule)
    }

    install(RoutingRoot)

    configureSockets()
    configureSecurity()
    configureHTTP()
    configureSerialization()
    configureDatabases()
    configureMonitoring()
    configureSwagger()
    configureRouting()
}

fun Application.configureSwagger() {
    routing {
        swaggerUI(path = "swagger", swaggerFile = "openapi/documentation.yaml")
        openAPI(path = "openapi", swaggerFile = "openapi/documentation.yaml")
    }
}
