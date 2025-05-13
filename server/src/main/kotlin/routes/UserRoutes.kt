package com.carspotter.routes

import com.carspotter.data.dto.CreateUserRequest
import com.carspotter.data.dto.UpdateProfilePictureRequest
import com.carspotter.data.dto.toResponse
import com.carspotter.data.dto.toUser
import com.carspotter.data.service.user.IUserService
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import org.koin.ktor.ext.inject
import kotlin.text.toIntOrNull

fun Route.userRoutes() {
    val userService: IUserService by inject()

    authenticate("jwt") {
        route("/users") {
            get("/{userId}") {
                val id = call.parameters["userId"]?.toIntOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid user ID")
                val user = userService.getUserByID(id)

                if (user != null) {
                    call.respond(user.toResponse())
                } else {
                    call.respond(HttpStatusCode.NotFound, "User not found")
                }
            }

            get("/all") {
                val principal = call.principal<JWTPrincipal>()
                val userRole = principal?.payload?.getClaim("role")?.asString()

                if (userRole != "admin") {
                    return@get call.respond(HttpStatusCode.Forbidden, "Access denied")
                }

                val users = userService.getAllUsers()
                call.respond(users.map { it.toResponse() })
            }

            get("/by-username/{username}") {
                val username = call.parameters["username"]
                    ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing username")
                val user = userService.getUserByUsername(username)

                if (user != null) {
                    call.respond(user.toResponse())
                } else {
                    call.respond(HttpStatusCode.NotFound, "User not found")
                }
            }

            post {
                val request = call.receive<CreateUserRequest>()
                val result = userService.createUser(request.toUser())
                if (result > 0) {
                    call.respond(HttpStatusCode.Created, "User created with ID: $result")
                } else {
                    call.respond(HttpStatusCode.InternalServerError, "Failed to create user")
                }
            }

            put("/profile-picture") {
                val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asInt()
                    ?: return@put call.respond(HttpStatusCode.Unauthorized, "Missing or invalid JWT token")

                val request = call.receive<UpdateProfilePictureRequest>()
                val result = userService.updateProfilePicture(userId, request.imagePath)

                if (result > 0) {
                    call.respond(HttpStatusCode.OK, "Profile picture updated")
                } else {
                    call.respond(HttpStatusCode.NotFound, "User not found")
                }
            }

            delete("/me") {
                val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asInt()
                    ?: return@delete call.respond(HttpStatusCode.Unauthorized, "Missing or invalid JWT token")

                val result = userService.deleteUser(userId)
                if (result > 0) {
                    call.respond(HttpStatusCode.OK, "User deleted")
                } else {
                    call.respond(HttpStatusCode.NotFound, "User not found")
                }
            }
        }
    }
}
