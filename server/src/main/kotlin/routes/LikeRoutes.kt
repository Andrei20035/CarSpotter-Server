package com.carspotter.routes

import com.carspotter.data.service.like.ILikeService
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.likeRoutes() {
    val likeService: ILikeService by inject()

    authenticate("jwt") {
        route("/like") {
            post("/{postId}") {
                val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asInt()
                    ?: return@post call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Missing or invalid JWT token"))

                val postId = call.parameters["postId"]?.toIntOrNull()
                    ?: return@post call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid or missing post ID"))

                val result = likeService.likePost(userId, postId)

                if(result > 0) {
                    return@post call.respond(HttpStatusCode.OK, mapOf("message" to "Post liked successfully"))
                } else {
                    return@post call.respond(HttpStatusCode.Conflict, mapOf("error" to "You have already liked this post"))
                }
            }

            delete("/{postId}") {
                val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asInt()
                    ?: return@delete call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Missing or invalid JWT token"))

                val postId = call.parameters["postId"]?.toIntOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid or missing post ID"))

                val rowsDeleted = likeService.unlikePost(userId, postId)

                if (rowsDeleted > 0) {
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Post unliked successfully"))
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Like not found or already removed"))
                }
            }

            get("/post/{postId}") {
                val postId = call.parameters["postId"]?.toIntOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid or missing post ID"))

                val users = likeService.getLikesForPost(postId)

                if (users.isEmpty()) {
                    call.respond(HttpStatusCode.NoContent, mapOf("error" to "No likes for this post"))
                } else {
                    call.respond(HttpStatusCode.OK, users)
                }
            }
        }
    }
}
