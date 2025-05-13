package com.carspotter.routes

import com.carspotter.data.service.like.ILikeService
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

fun Route.likeRoutes() {
    val likeService: ILikeService by inject()

    authenticate("jwt") {
        route("/like") {

            post("/{postId}") {
                val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asInt()
                    ?: return@post call.respond(HttpStatusCode.Unauthorized, "Missing or invalid JWT token")

                val postId = call.parameters["postId"]?.toIntOrNull()
                    ?: return@post call.respond(HttpStatusCode.BadRequest, "Invalid or missing post ID")

                val result = likeService.likePost(userId, postId)

                if(result > 0) {
                    return@post call.respond(HttpStatusCode.OK, mapOf("message" to "Post liked successfully"))
                } else {
                    return@post call.respond(HttpStatusCode.Conflict, mapOf("error" to "You have already liked this post"))
                }
            }

            delete("/{postId}") {
                val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asInt()
                    ?: return@delete call.respond(HttpStatusCode.Unauthorized, "Missing or invalid JWT token")

                val postId = call.parameters["postId"]?.toIntOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, "Invalid or missing post ID")

                val rowsDeleted = likeService.unlikePost(userId, postId)

                if (rowsDeleted > 0) {
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Post unliked successfully"))
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Like not found or already removed"))
                }
            }

            get("/post/{postId}") {
                val postId = call.parameters["postId"]?.toIntOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid or missing post ID")

                val users = likeService.getLikesForPost(postId)

                if (users.isEmpty()) {
                    call.respond(HttpStatusCode.NoContent, "No likes for this post")
                } else {
                    call.respond(HttpStatusCode.OK, users)
                }
            }
        }
    }
}
