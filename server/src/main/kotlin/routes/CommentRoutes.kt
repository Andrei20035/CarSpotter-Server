package com.carspotter.routes

import com.carspotter.data.dto.request.CommentRequest
import com.carspotter.data.service.comment.ICommentService
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
import io.ktor.server.routing.route
import org.koin.ktor.ext.inject

fun Route.commentRoutes() {
    val commentService: ICommentService by inject()
    val postService: IPostService by inject()

        get("/comment/{postId}") {
            val postId = call.parameters["postId"]?.toIntOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid post ID")

            val comments = commentService.getCommentsForPost(postId)

            if (comments.isEmpty()) {
                return@get call.respond(HttpStatusCode.NoContent, "No comments found for this post")
            }

            call.respond(HttpStatusCode.OK, comments)
        }

        authenticate("jwt") {
            route("/comment") {
                post {
                    val request = call.receive<CommentRequest>()
                    val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asInt()
                        ?: return@post call.respond(HttpStatusCode.Unauthorized, "Missing or invalid JWT token")

                    if (request.commentText.isBlank()) {
                        call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Comment text cannot be blank"))
                        return@post
                    }


                    val commentId = commentService.addComment(userId, request.postId, request.commentText)

                    if(commentId > 0) {
                        call.respond(HttpStatusCode.Created, mapOf("message" to "Comment created successfully"))
                        return@post
                    } else {
                        call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Failed to create comment"))
                        return@post
                    }
                }

                delete("/{commentId}") {
                    val commentId = call.parameters["commentId"]?.toIntOrNull()
                        ?: return@delete call.respond(HttpStatusCode.BadRequest, "Invalid comment ID")

                    val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asInt()
                        ?: return@delete call.respond(HttpStatusCode.Unauthorized, "Missing or invalid JWT token")

                    val comment = commentService.getCommentById(commentId)

                    if (comment == null) {
                        return@delete call.respond(HttpStatusCode.NotFound, "Comment not found")
                    }

                    val postOwnerId = postService.getUserIdByPost(comment.postId)

                    if (comment.userId != userId && postOwnerId != userId) {
                        return@delete call.respond(
                            HttpStatusCode.Forbidden,
                            "You are not authorized to delete this comment"
                        )
                    }

                    val rowsAffected = commentService.deleteComment(commentId)

                    if (rowsAffected > 0) {
                        call.respond(HttpStatusCode.OK, mapOf("message" to "Comment deleted successfully"))
                    } else {
                        call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Failed to delete comment"))
                    }
                }
            }
        }
    }