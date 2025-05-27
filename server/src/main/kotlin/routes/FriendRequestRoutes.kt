package com.carspotter.routes

import com.carspotter.data.service.friend_request.IFriendRequestService
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.friendRequestRoutes() {
    val friendRequestService: IFriendRequestService by application.inject()

    authenticate("jwt") {
        route("/friend-request") {
            authenticate("admin") {
                get("/admin") {
                    val principal = call.principal<JWTPrincipal>()
                    val isAdmin = principal?.getClaim("isAdmin", Boolean::class) ?: false
                    if (!isAdmin) {
                        call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Admin access required"))
                        return@get
                    }

                    val allRequests = friendRequestService.getAllFriendReqFromDB()
                    call.respond(HttpStatusCode.OK, allRequests )
                }
            }

            post("/{receiverId}") {
                val senderId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asInt()
                    ?: return@post call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid JWT token"))

                val receiverId = call.parameters["receiverId"]?.toIntOrNull()
                    ?: return@post call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid or missing receiverId"))

                if (senderId == receiverId) {
                    return@post call.respond(HttpStatusCode.BadRequest, mapOf("error" to "You cannot send a friend request to yourself"))
                }

                val rowsInserted = friendRequestService.sendFriendRequest(senderId, receiverId)

                if(rowsInserted == 1) {
                    call.respond(HttpStatusCode.Created, mapOf("message" to "Friend request sent"))
                } else {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "A problem occurred when sending friend request"))
                }
            }

            post("/{senderId}/accept") {
                val receiverId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asInt()
                    ?: return@post call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid JWT token"))

                val senderId = call.parameters["senderId"]?.toIntOrNull()
                    ?: return@post call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid or missing senderId"))

                if (senderId == receiverId) {
                    return@post call.respond(HttpStatusCode.BadRequest, mapOf("error" to "You cannot accept a friend request from yourself"))
                }

                val result = friendRequestService.acceptFriendRequest(senderId, receiverId)

                if (result) {
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Friend request accepted"))
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Friend request not found"))
                }
            }

            post("/{senderId}/decline") {
                val receiverId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asInt()
                    ?: return@post call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid JWT token"))

                val senderId = call.parameters["senderId"]?.toIntOrNull()
                    ?: return@post call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid or missing senderId"))

                if (senderId == receiverId) {
                    return@post call.respond(HttpStatusCode.BadRequest, mapOf("error" to "You cannot decline a friend request from yourself"))
                }

                val rowsAffected = friendRequestService.declineFriendRequest(senderId, receiverId)

                if (rowsAffected > 0) {
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Friend request declined"))
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Friend request not found"))
                }
            }

            get {
                val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asInt()
                    ?: return@get call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Missing or invalid JWT token"))

                val friendRequests = friendRequestService.getAllFriendRequests(userId)

                if (friendRequests.isEmpty()) {
                    return@get call.respond(HttpStatusCode.NoContent)
                }

                call.respond(HttpStatusCode.OK, friendRequests)
            }

        }
    }
}
