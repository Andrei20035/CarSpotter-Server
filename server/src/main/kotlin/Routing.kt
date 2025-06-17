package com.carspotter

import com.carspotter.routes.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.NotFoundException
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    install(StatusPages) {

        // --- Standard Exceptions ---
        exception<IllegalStateException> { call, cause ->
            call.respondText(
                text = "Illegal state: ${cause.message}",
                status = HttpStatusCode.InternalServerError
            )
        }

        exception<IllegalArgumentException> { call, cause ->
            call.respondText(
                text = "Invalid argument: ${cause.message}",
                status = HttpStatusCode.BadRequest
            )
        }

        exception<NotFoundException> { call, cause ->
            call.respondText(
                text = "Not found: ${cause.message}",
                status = HttpStatusCode.NotFound
            )
        }

        // --- Catch-All Exception Handler (for 500 errors) ---
        exception<Throwable> { call, cause ->
            call.respondText(
                text = "Internal server error: ${cause.localizedMessage ?: "Unexpected error"}",
                status = HttpStatusCode.InternalServerError
            )
            // Optional logging (uncomment in real app):
            // call.application.environment.log.error("Unhandled exception", cause)
        }

        // --- Common Status Codes ---
        status(HttpStatusCode.BadRequest) { call, _ ->
            call.respondText("Bad request", status = HttpStatusCode.BadRequest)
        }

        status(HttpStatusCode.Unauthorized) { call, _ ->
            call.respondText("Unauthorized", status = HttpStatusCode.Unauthorized)
        }

        status(HttpStatusCode.Forbidden) { call, _ ->
            call.respondText("Forbidden", status = HttpStatusCode.Forbidden)
        }

        status(HttpStatusCode.NotFound) { call, _ ->
            call.respondText("Resource not found", status = HttpStatusCode.NotFound)
        }

        status(HttpStatusCode.InternalServerError) { call, _ ->
            call.respondText("Server error", status = HttpStatusCode.InternalServerError)
        }
    }

    routing {
        // Static resources
        staticResources("/static", "static")

        // API routes
        route("/api") {
            authRoutes()
            carModelRoutes()
            commentRoutes()
            friendRequestRoutes()
            friendRoutes()
            likeRoutes()
            postRoutes()
            userCarRoutes()
            userRoutes()
            get("/") {
                call.respondText(
                    """Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt
                        | ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco
                        | laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit 
                        | in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat
                        | cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.""".trimMargin())
            }

        }
    }
}

