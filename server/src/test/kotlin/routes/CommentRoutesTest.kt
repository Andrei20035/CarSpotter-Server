package routes

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.carspotter.configureSerialization
import com.carspotter.data.dto.request.CommentRequest
import com.carspotter.data.dto.toDTO
import com.carspotter.data.model.Comment
import com.carspotter.data.model.Post
import com.carspotter.data.service.comment.ICommentService
import com.carspotter.data.service.post.IPostService
import com.carspotter.routes.authRoutes
import com.carspotter.routes.commentRoutes
import com.github.dockerjava.api.model.SELContext
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.response.respond
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.context.GlobalContext.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import java.util.Date
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CommentRoutesTest : KoinTest {

    private lateinit var commentService: ICommentService
    private lateinit var postService: IPostService

    @BeforeAll
    fun setup() {
        commentService = mockk()
        postService = mockk()
    }

    @BeforeEach
    fun setupKoin() {
        startKoin {
            modules(
                module {
                    single { commentService }
                    single { postService }
                }
            )
        }
    }

    private fun Application.configureTestApplication() {
        System.setProperty("JWT_SECRET", "test-secret-key")

        configureSerialization()

        install(Authentication) {
            jwt("jwt") {
                realm = "Test Server"
                verifier(
                    JWT
                        .require(Algorithm.HMAC256("test-secret-key"))
                        .build()
                )
                validate { credential ->
                    if (credential.payload.getClaim("userId").asInt() != null) {
                        JWTPrincipal(credential.payload)
                    } else {
                        null
                    }
                }
                challenge { _, _ ->
                    call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Missing or invalid JWT token"))
                }
            }
        }

        routing {
            commentRoutes()
        }
    }

    @AfterEach
    fun tearDownKoin() {
        stopKoin()
        clearAllMocks()
    }

    @Test
    fun `GET comments returns 400 for invalid post ID`() = testApplication {
        application {
            configureTestApplication()
        }

        val response = client.get("/comment/abc")

        assertEquals(HttpStatusCode.BadRequest, response.status)

        val expectedJson = Json.parseToJsonElement("""{"error":"Invalid post ID"}""").jsonObject
        val actualJson = Json.parseToJsonElement(response.bodyAsText()).jsonObject

        assertEquals(expectedJson, actualJson)
    }

    @Test
    fun `GET comments returns 204 for empty comment list`() = testApplication {
        coEvery { commentService.getCommentsForPost(1) } returns emptyList()

        application {
            configureTestApplication()
        }

        val response = client.get("/comment/1")

        assertEquals(HttpStatusCode.NoContent, response.status)

        val expectedJson = Json.parseToJsonElement("""{"error":"No comments found for this post"}""").jsonObject
        val actualJson = Json.parseToJsonElement(response.bodyAsText()).jsonObject

        assertEquals(expectedJson, actualJson)
    }

    @Test
    fun `GET comments returns 200 for non-empty comment list`() = testApplication {
        val commentList = listOf(
            Comment(id = 1, postId = 1, userId = 5, commentText = "Nice car!").toDTO(),
            Comment(id = 2, postId = 1, userId = 5, commentText = "Socate").toDTO()

        )

        coEvery { commentService.getCommentsForPost(1) } returns commentList

        application {
            configureTestApplication()
        }

        val response = client.get("/comment/1")

        assertEquals(HttpStatusCode.OK, response.status)

        val expectedJson = Json.encodeToJsonElement(commentList)
        val actualJson = Json.parseToJsonElement(response.bodyAsText())

        assertEquals(expectedJson, actualJson)

    }

    @Test
    fun `POST comment returns 401 when JWT missing or invalid`() = testApplication {
        application {
            configureTestApplication()
        }

        val request = CommentRequest(postId = 1, commentText = "Hello")

        val response = client.post("/comment") {
            contentType(ContentType.Application.Json)
            header(HttpHeaders.Authorization, "Bearer invalid-token")
            setBody(Json.encodeToString(request))
        }

        assertEquals(HttpStatusCode.Unauthorized, response.status)

        val expectedJson = Json.parseToJsonElement("""{"error":"Missing or invalid JWT token"}""").jsonObject
        val actualJson = Json.parseToJsonElement(response.bodyAsText()).jsonObject

        assertEquals(expectedJson, actualJson)
    }

    @Test
    fun `POST comment returns 400 when comment text is blank`() = testApplication {
        val userId = 1

        application {
            configureTestApplication()
        }

        val request = CommentRequest(postId = 1, commentText = "")

        val token = createTestToken(userId)

        val response = client.post("/comment") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(request))
            header(HttpHeaders.Authorization, "Bearer $token")
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)

        val expectedJson = Json.parseToJsonElement("""{"error":"Comment text cannot be blank"}""").jsonObject
        val actualJson = Json.parseToJsonElement(response.bodyAsText()).jsonObject

        assertEquals(actualJson, expectedJson)
    }

    @Test
    fun `POST comment returns 201 when comment is created successfully`() = testApplication {
        val userId = 1

        coEvery { commentService.addComment(userId, 1, "Hello!")} returns 1

        application {
            configureTestApplication()
        }

        val request = CommentRequest(postId = 1, commentText = "Hello!")

        val token = createTestToken(userId)

        val response = client.post("/comment") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(request))
            header(HttpHeaders.Authorization, "Bearer $token")
        }

        assertEquals(HttpStatusCode.Created, response.status)

        val expectedJson = Json.parseToJsonElement("""{"message":"Comment created successfully"}""").jsonObject
        val actualJson = Json.parseToJsonElement(response.bodyAsText()).jsonObject

        assertEquals(actualJson, expectedJson)

        coVerify(exactly = 1) { commentService.addComment(userId, 1, "Hello!")}
    }

    @Test
    fun `POST comment returns 500 when comment creation fails`() = testApplication {
        val userId = 1

        coEvery { commentService.addComment(userId, 1, "Hello!") } returns -1

        application {
            configureTestApplication()
        }

        val request = CommentRequest(postId = 1, commentText = "Hello!")

        val token = createTestToken(userId)
        val response = client.post("/comment") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(request))
            header(HttpHeaders.Authorization, "Bearer $token")
        }

        assertEquals(HttpStatusCode.InternalServerError, response.status)

        val expectedJson = Json.parseToJsonElement("""{"error":"Failed to create comment"}""").jsonObject
        val actualJson = Json.parseToJsonElement(response.bodyAsText()).jsonObject

        assertEquals(expectedJson, actualJson)

        coVerify(exactly = 1) { commentService.addComment(userId, 1, "Hello!") }
    }

    @Test
    fun `DELETE comment returns 400 when commentId is missing or invalid`() = testApplication {
        application {
            configureTestApplication()
        }

        val token = createTestToken(1)

        val response = client.delete("/comment/invalid-id") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }

        val expectedJson = Json.parseToJsonElement("""{"error":"Invalid comment ID"}""").jsonObject
        val actualJson = Json.parseToJsonElement(response.bodyAsText()).jsonObject

        assertEquals(expectedJson, actualJson)
    }

    @Test
    fun `DELETE comment returns 401 when JWT missing or invalid`() = testApplication {
        application {
            configureTestApplication()
        }

        val response = client.delete("/comment/1") {
            header(HttpHeaders.Authorization, "Bearer invalid-token")
        }

        assertEquals(HttpStatusCode.Unauthorized, response.status)

        val expectedJson = Json.parseToJsonElement("""{"error":"Missing or invalid JWT token"}""").jsonObject
        val actualJson = Json.parseToJsonElement(response.bodyAsText()).jsonObject

        assertEquals(expectedJson, actualJson)
    }

    @Test
    fun `DELETE comment returns 404 if comment not found`() = testApplication {
        coEvery { commentService.getCommentById(1) } returns null

        val token = createTestToken(1)

        application {
            configureTestApplication()
        }

        val response = client.delete("/comment/1") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }

        val expectedJson = Json.parseToJsonElement("""{"error":"Comment not found"}""").jsonObject
        val actualJson = Json.parseToJsonElement(response.bodyAsText()).jsonObject

        assertEquals(expectedJson, actualJson)

        coVerify(exactly = 1) { commentService.getCommentById(1) }
    }

    @Test
    fun `DELETE comment returns 403 when user is not owner or post owner`() = testApplication {
        val fakeComment = Comment(id = 1, postId = 1, userId = 2, commentText = "Hello").toDTO()

        coEvery { commentService.getCommentById(1) } returns fakeComment
        coEvery { postService.getUserIdByPost(1) } returns 3

        val token = createTestToken(4)

        application {
            configureTestApplication()
        }

        val response = client.delete("/comment/1") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }

        val expectedJson = Json.parseToJsonElement("""{"error":"You are not authorized to delete this comment"}""").jsonObject
        val actualJson = Json.parseToJsonElement(response.bodyAsText()).jsonObject

        assertEquals(expectedJson, actualJson)

        coVerify(exactly = 1) { commentService.getCommentById(1) }
        coVerify(exactly = 1) { postService.getUserIdByPost(1) }
    }

    @Test
    fun `DELETE comment returns 200 when successfully deleted`() = testApplication {
        val userId = 1
        val fakeComment = Comment(id = 1, postId = 1, userId = userId, commentText = "Hello").toDTO()

        coEvery { commentService.getCommentById(1) } returns fakeComment
        coEvery { postService.getUserIdByPost(1) } returns userId
        coEvery { commentService.deleteComment(1) } returns 1

        val token = createTestToken(userId = userId)

        application {
            configureTestApplication()
        }

        val response = client.delete("/comment/1") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }

        assertEquals(HttpStatusCode.OK, response.status)

        val expectedJson = Json.parseToJsonElement("""{"message":"Comment deleted successfully"}""").jsonObject
        val actualJson = Json.parseToJsonElement(response.bodyAsText()).jsonObject

        assertEquals(expectedJson, actualJson)

        coVerify(exactly = 1) { commentService.getCommentById(1) }
        coVerify(exactly = 1) { postService.getUserIdByPost(1) }
        coVerify(exactly = 1) { commentService.deleteComment(1) }
    }

    @Test
    fun `DELETE comment returns 500 when deletion fails`() = testApplication {
        val userId = 1
        val fakeComment = Comment(id = 1, postId = 1, userId = userId, commentText = "Hello").toDTO()

        coEvery { commentService.getCommentById(1) } returns fakeComment
        coEvery { postService.getUserIdByPost(1) } returns userId
        coEvery { commentService.deleteComment(1) } returns 0

        val token = createTestToken(userId = userId)

        application { configureTestApplication() }

        val response = client.delete("/comment/1") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }

        assertEquals(HttpStatusCode.InternalServerError, response.status)

        val expectedJson = Json.parseToJsonElement("""{"error":"Failed to delete comment"}""").jsonObject
        val actualJson = Json.parseToJsonElement(response.bodyAsText()).jsonObject

        assertEquals(expectedJson, actualJson)

        coVerify(exactly = 1) { commentService.getCommentById(1) }
        coVerify(exactly = 1) { postService.getUserIdByPost(1) }
        coVerify(exactly = 1) { commentService.deleteComment(1) }
    }

    private fun createTestToken(userId: Int): String {
        return JWT.create()
            .withClaim("userId", userId)
            .withExpiresAt(Date(System.currentTimeMillis() + 60000))
            .sign(Algorithm.HMAC256(System.getenv("JWT_SECRET") ?: "test-secret-key"))
    }
}