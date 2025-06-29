package com.carspotter

import com.carspotter.routes.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
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
            uploadRoutes()
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

