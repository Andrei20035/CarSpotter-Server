package com.carspotter

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.carspotter.data.dto.AuthCredentialDTO
import com.carspotter.data.dto.GoogleLoginRequest
import com.carspotter.data.dto.LoginRequest
import com.carspotter.data.dto.RegisterRequest
import com.carspotter.data.dto.UpdatePasswordRequest
import com.carspotter.data.model.AuthCredential
import com.carspotter.data.model.Post
import com.carspotter.data.service.auth_credential.IAuthCredentialService
import com.carspotter.data.service.user.IUserService
import com.carspotter.routes.authRoutes
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.authenticate
import io.ktor.server.http.content.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.util.Date

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
            // Auth routes
            authRoutes()

            // User routes (protected)
            authenticate("jwt") {
                route("/users") {
                    get("/{userId}") {
                        // Get user by ID
                        val id = call.parameters["userId"]?.toIntOrNull() ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid user ID")
                        val userService = call.application.inject<IUserService>().value
                        val user = userService.getUserByID(id)

                        if (user != null) {
                            call.respond(user)
                        } else {
                            call.respond(HttpStatusCode.NotFound, "User not found")
                        }
                    }

                    put("/{id}/profile-picture") {
                        // Update profile picture
                        val id = call.parameters["id"]?.toIntOrNull() ?: throw IllegalArgumentException("Invalid ID")
                        val request = call.receive<UpdateProfilePictureRequest>()
                        val userService = call.application.inject<IUserService>().value

                        val result = userService.updateProfilePicture(id, request.imagePath)
                        if (result > 0) {
                            call.respond(HttpStatusCode.OK)
                        } else {
                            call.respond(HttpStatusCode.NotFound, "User not found")
                        }
                    }

                    delete("/{id}") {
                        // Delete user
                        val id = call.parameters["id"]?.toIntOrNull() ?: throw IllegalArgumentException("Invalid ID")
                        val userService = call.application.inject<IUserService>().value

                        val result = userService.deleteUser(id)
                        if (result > 0) {
                            call.respond(HttpStatusCode.OK)
                        } else {
                            call.respond(HttpStatusCode.NotFound, "User not found")
                        }
                    }
                }

                // Posts routes
//                route("/posts") {
//                    get {
//                        val postService = call.application.inject<IPostService>().value
//                        call.respond(postService.getAllPosts())
//                    }
//
//                    get("/{id}") {
//                        val id = call.parameters["id"]?.toIntOrNull() ?: throw IllegalArgumentException("Invalid ID")
//                        val postService = call.application.inject<IPostService>().value
//                        val post = postService.getPostById(id) ?: throw NotFoundException("Post not found")
//                        call.respond(post)
//                    }
//
//                    post {
//                        val request = call.receive<CreatePostRequest>()
//                        val postService = call.application.inject<IPostService>().value
//
//                        val postId = postService.createPost(request.toPost())
//                        call.respond(HttpStatusCode.Created, mapOf("postId" to postId))
//                    }

                    // Add more post-routes (update, delete)
                }

                // Add routes for other entities (comments, likes, friends, etc.)
                // ...
//            }
        }
    }
}

// Helper function to generate JWT token


// Data classes for requests
data class UpdateProfilePictureRequest(val imagePath: String)
//data class CreatePostRequest(val userId: Int, val content: String, val imagePath: String?) {
//    fun toPost() =
//        Post(userId = userId, content = content, imagePath = imagePath, timestamp = System.currentTimeMillis())
//}

class NotFoundException(message: String) : RuntimeException(message)
