package com.carspotter.routes

import com.carspotter.data.dto.request.UserCarRequest
import com.carspotter.data.dto.request.UserCarUpdateRequest
import com.carspotter.data.dto.request.toUserCar
import com.carspotter.data.service.user_car.IUserCarService
import com.carspotter.data.service.user_car.UserCarCreationException
import com.carspotter.utils.getUuidClaim
import com.carspotter.utils.toUuidOrNull
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.koin.ktor.ext.inject

fun Route.userCarRoutes() {
    val userCarService: IUserCarService by application.inject()

    authenticate("jwt") {
        route("/user-cars") {
            post {
                val userId = call.getUuidClaim("userId")
                    ?: return@post call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid or missing userId"))

                val userCarRequest = call.receive<UserCarRequest>()
                val userCar = userCarRequest.toUserCar(userId)

                try {
                    userCarService.createUserCar(userCar)
                    return@post call.respond(HttpStatusCode.Created, mapOf("message" to "User car created successfully"))

                } catch (e: UserCarCreationException) {
                    return@post call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
                } catch (e: ExposedSQLException) {
                    return@post call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("error" to "Invalid userId or carModelId")
                    )
                } catch (e: Exception) {
                    return@post call.respond(
                        HttpStatusCode.InternalServerError,
                        mapOf("error" to "Failed to create user car")
                    )
                }
            }

            get("/{userCarId}") {
                val userCarId = call.parameters["userCarId"].toUuidOrNull()
                    ?: return@get call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("error" to "Invalid or missing user car ID")
                    )

                val userCar = userCarService.getUserCarById(userCarId)

                if (userCar != null) {
                    return@get call.respond(HttpStatusCode.OK, userCar)
                } else {
                    return@get call.respond(HttpStatusCode.NotFound, mapOf("error" to "User car not found"))
                }
            }

            get("/by-user/{userId}") {
                val userId = call.parameters["userId"].toUuidOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid user ID"))

                val userCar = userCarService.getUserCarByUserId(userId)

                if (userCar != null) {
                    return@get call.respond(HttpStatusCode.OK, userCar)
                } else {
                    return@get call.respond(HttpStatusCode.NotFound, mapOf("error" to "User car not found"))
                }
            }

            get("/{userCarId}/user") {
                val userCarId = call.parameters["userCarId"].toUuidOrNull()
                    ?: return@get call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("error" to "Invalid or missing user car ID")
                    )

                val user = userCarService.getUserByUserCarId(userCarId)

                call.respond(HttpStatusCode.OK, user)
            }

            put {
                val userId = call.getUuidClaim("userId")
                    ?: return@put call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid or missing userId"))

                val request = call.receive<UserCarUpdateRequest>()

                val updatedRows = userCarService.updateUserCar(userId, request.imagePath, request.carModelId)

                if (updatedRows > 0) {
                    call.respond(HttpStatusCode.OK, mapOf("message" to "User car updated successfully"))
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "User car not found"))
                }
            }

            delete {
                val userId = call.getUuidClaim("userId")
                    ?: return@delete call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid or missing userId"))

                val deletedRows = userCarService.deleteUserCar(userId)

                if (deletedRows > 0) {
                    return@delete call.respond(HttpStatusCode.OK, mapOf("message" to "User car deleted successfully"))
                } else {
                    return@delete call.respond(HttpStatusCode.NotFound, mapOf("error" to "User car not found"))
                }
            }

            get {
                val allUserCars = userCarService.getAllUserCars()
                call.respond(HttpStatusCode.OK, allUserCars)
            }
        }
    }
}

