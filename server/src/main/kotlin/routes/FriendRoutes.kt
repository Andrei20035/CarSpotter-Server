package com.carspotter.routes

import com.carspotter.data.service.friend.IFriendService
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.friendRoutes() {
    val friendService: IFriendService by inject()

    authenticate("jwt") {
        route("/friend") {
            authenticate("admin") {
                get("/all") {
                    val allFriends = friendService.getAllFriendsInDb()
                    call.respond(HttpStatusCode.OK, allFriends)
                }
            }

            post("/{friendId}") {
                val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asInt()
                    ?: return@post call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Missing or invalid JWT token"))

                val friendId = call.parameters["friendId"]?.toIntOrNull()
                    ?: return@post call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid or missing friendId"))

                if (userId == friendId) {
                    return@post call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Cannot add yourself as a friend"))
                }

                val result = friendService.addFriend(userId, friendId)

                if(result != friendId) {
                    return@post call.respond(HttpStatusCode.Conflict, mapOf("error" to "Friendship already exists"))
                } else {
                    return@post call.respond(HttpStatusCode.Created, mapOf("message" to "Friend added"))
                }
            }

            delete("/{friendId}") {
                val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asInt()
                    ?: return@delete call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Missing or invalid JWT token"))

                val friendId = call.parameters["friendId"]?.toIntOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid or missing friendId"))

                val deletedRows = friendService.deleteFriend(userId, friendId)

                if(deletedRows == 2) {
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Friend deleted"))
                }
            }

            get {
                val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asInt()
                    ?: return@get call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Missing or invalid JWT token"))

                val friends = friendService.getAllFriends(userId)
                call.respond(HttpStatusCode.OK, friends)
            }

        }
    }
}
