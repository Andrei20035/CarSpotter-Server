package com.carspotter.routes

import com.carspotter.data.dto.request.PostEditRequest
import com.carspotter.data.dto.request.PostRequest
import com.carspotter.data.dto.request.toPost
import com.carspotter.data.service.post.IPostService
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
import org.koin.ktor.ext.inject
import java.time.ZoneId

fun Route.postRoutes() {
    val postService: IPostService by inject()

    authenticate("jwt") {
        route("/post") {
            post("/create") {
                val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asInt()
                    ?: return@post call.respond(HttpStatusCode.Unauthorized, "Missing or invalid JWT token")

                val postRequest = call.receive<PostRequest>()

                if(postRequest.imagePath.isBlank()) {
                    return@post call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Image path cannot be blank"))
                }

                val postId = postService.createPost(postRequest.toPost(userId))

                if(postId > 0) {
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Post created successfully", "postId" to postId))
                } else {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Failed to create post due to invalid input"))
                }
            }

            get("/{postId}") {
                val postId = call.parameters["postId"]?.toIntOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid or missing post ID")

                val post = postService.getPostById(postId)

                if (post != null) {
                    return@get call.respond(HttpStatusCode.OK, post)
                } else {
                    return@get call.respond(HttpStatusCode.NotFound, "Post not found")
                }
            }

            get("/all") {
                val posts = postService.getAllPosts()
                return@get call.respond(HttpStatusCode.OK, posts)
            }

            get("/current-day/{userId}") {
                val userId = call.parameters["userId"]?.toIntOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid or missing user ID")

                val userTimeZone = ZoneId.of(call.request.headers["Time-Zone"] ?: "UTC")  // Default to UTC if not specified

                val posts = postService.getCurrentDayPostsForUser(userId, userTimeZone)
                call.respond(HttpStatusCode.OK, posts)
            }

            // Route to edit a post
            put("/edit/{postId}") {
                val postId = call.parameters["postId"]?.toIntOrNull()
                    ?: return@put call.respond(HttpStatusCode.BadRequest, "Invalid or missing post ID")

                val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asInt()
                    ?: return@put call.respond(HttpStatusCode.Unauthorized, "Missing or invalid JWT token")

                val request = call.receive<PostEditRequest>()

                if(postService.getUserIdByPost(postId) != userId) {
                    return@put call.respond(HttpStatusCode.Forbidden, "You do not have permission to edit this post")
                }

                val updatedRows = postService.editPost(postId, request.newDescription)

                if (updatedRows > 0) {
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Post updated successfully"))
                } else {
                    call.respond(HttpStatusCode.NotFound, "Post not found or failed to update")
                }
            }

            delete("/{postId}") {
                val postId = call.parameters["postId"]?.toIntOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, "Invalid or missing post ID")

                val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asInt()
                    ?: return@delete call.respond(HttpStatusCode.Unauthorized, "Missing or invalid JWT token")

                if(postService.getUserIdByPost(postId) != userId) {
                    return@delete call.respond(HttpStatusCode.Forbidden, "You do not have permission to edit this post")
                }

                val deletedRows = postService.deletePost(postId)

                if (deletedRows > 0) {
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Post deleted successfully"))
                } else {
                    call.respond(HttpStatusCode.NotFound, "Post not found or already deleted")
                }
            }

        }
    }
}
