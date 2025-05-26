package com.carspotter.routes

import com.carspotter.data.dto.request.CreateUserRequest
import com.carspotter.data.dto.request.UpdateProfilePictureRequest
import com.carspotter.data.dto.request.toUser
import com.carspotter.data.service.user.IUserService
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.userRoutes() {
    val userService: IUserService by inject()

    authenticate("jwt") {
        route("/user") {
            get("/me") {
                val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asInt()
                    ?: return@get call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Missing or invalid JWT token"))

                val user = userService.getUserById(userId)

                if (user != null) {
                    return@get call.respond(user)
                } else {
                    return@get call.respond(HttpStatusCode.NotFound, mapOf("error" to "User not found"))
                }
            }

            get("/all") {
                val userRole = call.principal<JWTPrincipal>()?.payload?.getClaim("role")?.asString()

                if (userRole != "admin") {
                    return@get call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Access denied"))
                }

                val users = userService.getAllUsers()
                call.respond(users)
            }

            get("/by-username/{username}") {
                val username = call.parameters["username"]
                    ?: return@get call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Missing username"))

                val users = userService.getUserByUsername(username)

                return@get call.respond(HttpStatusCode.OK, users)
            }

            post {
                val request = call.receive<CreateUserRequest>()
                val result = userService.createUser(request.toUser())
                if (result > 0) {
                    return@post call.respond(HttpStatusCode.Created, mapOf("message" to "User created with ID: $result"))
                } else {
                    return@post call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Failed to create user"))
                }
            }

            put("/profile-picture") {
                val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asInt()
                    ?: return@put call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Missing or invalid JWT token"))

                val request = call.receive<UpdateProfilePictureRequest>()
                val result = userService.updateProfilePicture(userId, request.imagePath)

                if (result > 0) {
                    call.respond(HttpStatusCode.OK, mapOf("error" to "Profile picture updated"))
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "User not found"))
                }
            }

            delete("/me") {
                val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asInt()
                    ?: return@delete call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Missing or invalid JWT token"))

                val result = userService.deleteUser(userId)

                if (result > 0) {
                    call.respond(HttpStatusCode.OK, mapOf("message" to "User deleted"))
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "User not found"))
                }
            }
        }
    }
}
