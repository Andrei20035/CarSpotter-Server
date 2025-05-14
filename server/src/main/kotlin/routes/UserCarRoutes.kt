package com.carspotter.routes

import com.carspotter.data.service.user_car.IUserCarService
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
import jdk.internal.vm.ScopedValueContainer.call
import org.koin.ktor.ext.inject

fun Route.userCarRoutes() {
    val userCarService: IUserCarService by inject()

    authenticate("jwt") {
//        route("/user-car") {
//
//            // Create a new user car
//            post("/create") {
//                val principal = call.principal<JWTPrincipal>()
//                val userId = principal?.payload?.getClaim("userId")?.asInt()
//                    ?: return@post call.respond(HttpStatusCode.Unauthorized, "Missing or invalid JWT token")
//
//                val userCarRequest = call.receive<UserCarRequest>()
//                val userCar = userCarRequest.toUserCar(userId)
//
//                val userCarId = userCarService.createUserCar(userCar)
//
//                if (userCarId > 0) {
//                    call.respond(HttpStatusCode.Created, mapOf("message" to "User car created successfully", "userCarId" to userCarId))
//                } else {
//                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Failed to create user car"))
//                }
//            }
//
//            // Get user car by ID
//            get("/{userCarId}") {
//                val userCarId = call.parameters["userCarId"]?.toIntOrNull()
//                    ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid or missing user car ID")
//
//                val userCar = userCarService.getUserCarById(userCarId)
//
//                if (userCar != null) {
//                    call.respond(HttpStatusCode.OK, userCar)
//                } else {
//                    call.respond(HttpStatusCode.NotFound, "User car not found")
//                }
//            }
//
//            // Get user car by user ID
//            get("/user/{userId}") {
//                val userId = call.parameters["userId"]?.toIntOrNull()
//                    ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid or missing user ID")
//
//                val userCar = userCarService.getUserCarByUserId(userId)
//
//                if (userCar != null) {
//                    call.respond(HttpStatusCode.OK, userCar)
//                } else {
//                    call.respond(HttpStatusCode.NotFound, "User car not found")
//                }
//            }
//
//            // Get user by user car ID
//            get("/user-by-car/{userCarId}") {
//                val userCarId = call.parameters["userCarId"]?.toIntOrNull()
//                    ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid or missing user car ID")
//
//                val user = userCarService.getUserByUserCarId(userCarId)
//
//                if (user != null) {
//                    call.respond(HttpStatusCode.OK, user)
//                } else {
//                    call.respond(HttpStatusCode.NotFound, "User not found")
//                }
//            }
//
//            // Update user car
//            put("/update/{userId}") {
//                val userId = call.parameters["userId"]?.toIntOrNull()
//                    ?: return@put call.respond(HttpStatusCode.BadRequest, "Invalid or missing user ID")
//
//                val updatedCar = call.receive<UserCarUpdateRequest>()
//
//                userCarService.updateUserCar(userId, updatedCar.imagePath, updatedCar.carModelId)
//
//                call.respond(HttpStatusCode.OK, mapOf("message" to "User car updated successfully"))
//            }
//
//            // Delete user car
//            delete("/{userId}") {
//                val userId = call.parameters["userId"]?.toIntOrNull()
//                    ?: return@delete call.respond(HttpStatusCode.BadRequest, "Invalid or missing user ID")
//
//                userCarService.deleteUserCar(userId)
//
//                call.respond(HttpStatusCode.NoContent, mapOf("message" to "User car deleted successfully"))
//            }
//
//            // Get all user cars
//            get("/all") {
//                val allUserCars = userCarService.getAllUserCars()
//                call.respond(HttpStatusCode.OK, allUserCars)
//            }
//        }
    }
}