package com.carspotter.routes

import com.carspotter.data.dto.toResponse
import com.carspotter.data.service.friend.IFriendService
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import org.koin.ktor.ext.inject

fun Route.friendRoutes() {
    val friendService: IFriendService by inject()

    authenticate("jwt") {
        route("/friend") {
            authenticate("admin") {
                get("/all") {
                    val allFriends = friendService.getAllFriendsInDb()
                    call.respond(HttpStatusCode.OK, allFriends.map { it.toResponse() })
                }
            }

            post("/{friendId}") {
                val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asInt()
                    ?: return@post call.respond(HttpStatusCode.Unauthorized, "Missing or invalid JWT token")

                val friendId = call.parameters["friendId"]?.toIntOrNull()
                    ?: return@post call.respond(HttpStatusCode.BadRequest, "Invalid or missing friendId")

                if (userId == friendId) {
                    return@post call.respond(HttpStatusCode.BadRequest, "Cannot add yourself as a friend")
                }

                val result = friendService.addFriend(userId, friendId)
                call.respond(HttpStatusCode.Created, mapOf("message" to "Friend added", "id" to result))
            }

            delete("/{friendId}") {
                val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asInt()
                    ?: return@delete call.respond(HttpStatusCode.Unauthorized, "Missing or invalid JWT token")

                val friendId = call.parameters["friendId"]?.toIntOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, "Invalid or missing friendId")

                val result = friendService.deleteFriend(userId, friendId)
                call.respond(HttpStatusCode.OK, mapOf("message" to "Friend deleted", "deleted" to result))
            }

            get {
                val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asInt()
                    ?: return@get call.respond(HttpStatusCode.Unauthorized, "Missing or invalid JWT token")

                val friends = friendService.getAllFriends(userId)
                call.respond(HttpStatusCode.OK, friends.map { it.toResponse() })
            }

        }
    }
}
