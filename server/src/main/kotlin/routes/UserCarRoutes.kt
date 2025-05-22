package com.carspotter.routes

import com.carspotter.data.dto.request.UserCarRequest
import com.carspotter.data.dto.request.UserCarUpdateRequest
import com.carspotter.data.dto.request.toUserCar
import com.carspotter.data.service.user_car.IUserCarService
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.koin.ktor.ext.inject

fun Route.userCarRoutes() {
    val userCarService: IUserCarService by inject()

    authenticate("jwt") {
        route("/user-car") {
            post("/create") {
                val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asInt()
                    ?: return@post call.respond(HttpStatusCode.Unauthorized, "Missing or invalid JWT token")

                val userCarRequest = call.receive<UserCarRequest>()
                val userCar = userCarRequest.toUserCar(userId)

                try {
                    val userCarId = userCarService.createUserCar(userCar)

                    if (userCarId > 0) {
                        return@post call.respond(HttpStatusCode.Created, mapOf("message" to "User car created successfully", "userCarId" to userCarId))
                    } else {
                        return@post call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Failed to create user car"))
                    }
                } catch (e: ExposedSQLException) {
                    return@post call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid userId or carModelId", "details" to e.message))
                } catch (e: Exception) {
                    return@post call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Unexpected error", "details" to e.message))
                }
            }

            get("/{userCarId}") {
                val userCarId = call.parameters["userCarId"]?.toIntOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid or missing user car ID")

                val userCar = userCarService.getUserCarById(userCarId)

                if (userCar != null) {
                    return@get call.respond(HttpStatusCode.OK, userCar)
                } else {
                    return@get call.respond(HttpStatusCode.NotFound, "User car not found")
                }
            }

            get("/user/{userId}") {
                val userId = call.parameters["userId"]?.toIntOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid or missing user ID")

                val userCar = userCarService.getUserCarByUserId(userId)

                if (userCar != null) {
                    return@get call.respond(HttpStatusCode.OK, userCar)
                } else {
                    return@get call.respond(HttpStatusCode.NotFound, "User car not found")
                }
            }

            get("/user-by-car/{userCarId}") {
                val userCarId = call.parameters["userCarId"]?.toIntOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid or missing user car ID")

                val user = userCarService.getUserByUserCarId(userCarId)

                call.respond(HttpStatusCode.OK, user)
            }

            put("/update") {
                val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asInt()
                    ?: return@put call.respond(HttpStatusCode.Unauthorized, "Missing or invalid JWT token")

                val request = call.receive<UserCarUpdateRequest>()

                val updatedRows = userCarService.updateUserCar(userId, request.imagePath, request.carModelId)

                if(updatedRows > 0) {
                    call.respond(HttpStatusCode.OK, mapOf("message" to "User car updated successfully"))
                } else {
                    call.respond(HttpStatusCode.NotFound, "User car not found")
                }
            }

            delete("/delete") {
                val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asInt()
                    ?: return@delete call.respond(HttpStatusCode.Unauthorized, "Missing or invalid JWT token")

                val deletedRows = userCarService.deleteUserCar(userId)

                if(deletedRows > 0) {
                    return@delete call.respond(HttpStatusCode.OK, mapOf("message" to "User car deleted successfully"))
                } else {
                    return@delete call.respond(HttpStatusCode.NotFound, "User car not found")
                }
            }

            get("/all") {
                val allUserCars = userCarService.getAllUserCars()
                call.respond(HttpStatusCode.OK, allUserCars)
            }
        }
    }
}