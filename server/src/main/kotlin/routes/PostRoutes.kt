package com.carspotter.routes

import com.carspotter.data.dto.request.PostEditRequest
import com.carspotter.data.dto.request.PostRequest
import com.carspotter.data.dto.request.toPost
import com.carspotter.data.service.post.IPostService
import com.carspotter.utils.getUuidClaim
import com.carspotter.utils.toUuidOrNull
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.time.ZoneId

fun Route.postRoutes() {
    val postService: IPostService by application.inject()

    authenticate("jwt") {
        route("/posts") {
            post {
                val userId = call.getUuidClaim("userId")
                    ?: return@post call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid or missing userId"))

                val postRequest = call.receive<PostRequest>()

                if(postRequest.imagePath.isBlank()) {
                    return@post call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Image path cannot be blank"))
                }

                try {
                    postService.createPost(postRequest.toPost(userId))
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Post created successfully"))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Failed to create post due to invalid input"))
                }
            }

            get("/{postId}") {
                val postId = call.parameters["postId"].toUuidOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid postId"))

                val post = postService.getPostById(postId)

                if (post != null) {
                    return@get call.respond(HttpStatusCode.OK, post)
                } else {
                    return@get call.respond(HttpStatusCode.NotFound, mapOf("error" to "Post not found"))
                }
            }

            get {
                val posts = postService.getAllPosts()
                return@get call.respond(HttpStatusCode.OK, posts)
            }

            get("/current-day") {
                val userId = call.getUuidClaim("userId")
                    ?: return@get call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid or missing userId"))

                val userTimeZone = ZoneId.of(call.request.headers["Time-Zone"] ?: "UTC")  // Default to UTC if not specified

                val posts = postService.getCurrentDayPostsForUser(userId, userTimeZone)
                call.respond(HttpStatusCode.OK, posts)
            }

            put("/{postId}") {
                val postId = call.parameters["postId"].toUuidOrNull()
                    ?: return@put call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid or missing postId"))

                val userId = call.getUuidClaim("userId")
                    ?: return@put call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid or missing userId"))

                val request = call.receive<PostEditRequest>()

                if(postService.getUserIdByPost(postId) != userId) {
                    return@put call.respond(HttpStatusCode.Forbidden, mapOf("error" to "You do not have permission to edit this post"))
                }

                val updatedRows = postService.editPost(postId, request.newDescription)

                if (updatedRows > 0) {
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Post updated successfully"))
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Post not found or failed to update"))
                }
            }

            delete("/{postId}") {
                val postId = call.parameters["postId"].toUuidOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid postId"))

                val userId = call.getUuidClaim("userId")
                    ?: return@delete call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid invalid JWT token"))

                if(postService.getUserIdByPost(postId) != userId) {
                    return@delete call.respond(HttpStatusCode.Forbidden, mapOf("error" to "You do not have permission to edit this post"))
                }

                val deletedRows = postService.deletePost(postId)

                if (deletedRows > 0) {
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Post deleted successfully"))
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Post not found or already deleted"))
                }
            }

        }
    }
}
