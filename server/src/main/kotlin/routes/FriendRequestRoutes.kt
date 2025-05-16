package com.carspotter.routes

import com.carspotter.data.service.friend_request.IFriendRequestService
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import org.koin.ktor.ext.inject

fun Route.friendRequestRoutes() {
    val friendRequestService: IFriendRequestService by inject()

    authenticate("jwt") {
        route("/friend-request") {
            authenticate("admin") {
                get("/all") {
                    val allRequests = friendRequestService.getAllFriendReqFromDB()
                    call.respond(HttpStatusCode.OK, allRequests )
                }
            }

            post("/{receiverId}") {
                val senderId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asInt()
                    ?: return@post call.respond(HttpStatusCode.Unauthorized, "Missing or invalid JWT token")

                val receiverId = call.parameters["receiverId"]?.toIntOrNull()
                    ?: return@post call.respond(HttpStatusCode.BadRequest, "Invalid or missing receiverId")

                if (senderId == receiverId) {
                    return@post call.respond(HttpStatusCode.BadRequest, "You cannot send a friend request to yourself")
                }

                friendRequestService.sendFriendRequest(senderId, receiverId)
                call.respond(HttpStatusCode.Created, mapOf("message" to "Friend request sent", "senderId" to senderId, "receiverId" to receiverId))
            }

            post("/accept/{senderId}") {
                val receiverId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asInt()
                    ?: return@post call.respond(HttpStatusCode.Unauthorized, "Missing or invalid JWT token")

                val senderId = call.parameters["senderId"]?.toIntOrNull()
                    ?: return@post call.respond(HttpStatusCode.BadRequest, "Invalid or missing senderId")

                if (senderId == receiverId) {
                    return@post call.respond(HttpStatusCode.BadRequest, "You cannot accept a friend request from yourself")
                }

                val result = friendRequestService.acceptFriendRequest(senderId, receiverId)

                if (result) {
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Friend request accepted"))
                } else {
                    call.respond(HttpStatusCode.NotFound, "Friend request not found")
                }
            }

            post("/decline/{senderId}") {
                val receiverId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asInt()
                    ?: return@post call.respond(HttpStatusCode.Unauthorized, "Missing or invalid JWT token")

                val senderId = call.parameters["senderId"]?.toIntOrNull()
                    ?: return@post call.respond(HttpStatusCode.BadRequest, "Invalid or missing senderId")

                if (senderId == receiverId) {
                    return@post call.respond(HttpStatusCode.BadRequest, "You cannot decline a friend request from yourself")
                }

                val rowsAffected = friendRequestService.declineFriendRequest(senderId, receiverId)

                if (rowsAffected > 0) {
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Friend request declined"))
                } else {
                    call.respond(HttpStatusCode.NotFound, "Friend request not found")
                }
            }

            get("/requests") {
                val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asInt()
                    ?: return@get call.respond(HttpStatusCode.Unauthorized, "Missing or invalid JWT token")

                val friendRequests = friendRequestService.getAllFriendRequests(userId)
                if (friendRequests.isEmpty()) {
                    return@get call.respond(HttpStatusCode.NoContent, "No friend requests found")
                }

                call.respond(HttpStatusCode.OK, friendRequests)
            }

        }
    }
}
