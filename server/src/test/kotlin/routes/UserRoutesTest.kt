package routes

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.carspotter.configureSerialization
import com.carspotter.data.dto.UserDTO
import com.carspotter.data.dto.request.CreateUserRequest
import com.carspotter.data.dto.request.UpdateProfilePictureRequest
import com.carspotter.data.service.auth_credential.JwtService
import com.carspotter.data.service.user.IUserService
import com.carspotter.routes.userRoutes
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject
import org.junit.jupiter.api.*
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import java.time.LocalDate
import java.util.*
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserRoutesTest : KoinTest {

    private lateinit var userService: IUserService
    private lateinit var jwtService: JwtService

    @BeforeAll
    fun setup() {
        userService = mockk()
        jwtService = mockk()
    }

    @BeforeEach
    fun setupKoin() {
        startKoin {
            modules(
                module {
                    single { userService }
                    single { jwtService }
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
                    call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid or missing JWT token"))
                }
            }
            jwt("admin") {
                realm = "Test Server"
                verifier(
                    JWT
                        .require(Algorithm.HMAC256("test-secret-key"))
                        .build()
                )
                validate { credential ->
                    if (credential.payload.getClaim("userId").asInt() != null &&
                        credential.payload.getClaim("isAdmin").asBoolean() == true) {
                        JWTPrincipal(credential.payload)
                    } else {
                        null
                    }
                }
                challenge { _, _ ->
                    call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Admin access required"))
                }
            }
        }

        routing {
            userRoutes()
        }
    }

    @AfterEach
    fun tearDownKoin() {
        stopKoin()
        clearAllMocks()
    }

    @Test
    fun `GET me returns 401 when no jwt provided`() = testApplication {
        application {
            configureTestApplication()
        }

        val response = client.get("/user/me")

        assertEquals(HttpStatusCode.Unauthorized, response.status)

        val expectedJson = Json.parseToJsonElement("""{"error":"Invalid or missing JWT token"}""").jsonObject
        val actualJson = Json.parseToJsonElement(response.bodyAsText()).jsonObject

        assertEquals(expectedJson, actualJson)
    }

    @Test
    fun `GET me returns 404 when user not found`() = testApplication {
        val userId = 1

        coEvery { userService.getUserById(userId) } returns null

        application {
            configureTestApplication()
        }

        val token = createTestToken(userId, email = "test@yahoo.com", credentialId = 1)

        val response = client.get("/user/me") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }

        assertEquals(HttpStatusCode.NotFound, response.status)

        val expectedJson = Json.parseToJsonElement("""{"error":"User not found"}""").jsonObject
        val actualJson = Json.parseToJsonElement(response.bodyAsText()).jsonObject

        assertEquals(expectedJson, actualJson)

        coVerify(exactly = 1) { userService.getUserById(userId) }
    }

    @Test
    fun `GET me returns 200 with user data when user found`() = testApplication {
        val userId = 1
        val user = UserDTO(
            id = userId,
            firstName = "Test",
            lastName = "User",
            username = "testuser",
            profilePicturePath = "path/to/picture.jpg",
            birthDate = LocalDate.of(1990, 1, 1),
            country = "USA"
        )

        coEvery { userService.getUserById(userId) } returns user

        application {
            configureTestApplication()
        }

        val token = createTestToken(userId, email = "test@yahoo.com", credentialId = 1)

        val response = client.get("/user/me") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }

        assertEquals(HttpStatusCode.OK, response.status)

        val expectedJson = Json.encodeToJsonElement(user)
        val actualJson = Json.parseToJsonElement(response.bodyAsText())

        assertEquals(expectedJson, actualJson)

        coVerify(exactly = 1) { userService.getUserById(userId) }
    }

    @Test
    fun `GET all returns 403 when user is not admin`() = testApplication {
        application {
            configureTestApplication()
        }

        val token = createTestToken(1, false, email = "test@yahoo.com", credentialId = 1)

        val response = client.get("/user/all") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }

        assertEquals(HttpStatusCode.Forbidden, response.status)

        val expectedJson = Json.parseToJsonElement("""{"error":"Admin access required"}""").jsonObject
        val actualJson = Json.parseToJsonElement(response.bodyAsText()).jsonObject

        assertEquals(expectedJson, actualJson)
    }

    @Test
    fun `GET all returns 200 with all users when user is admin`() = testApplication {
        val users = listOf(
            UserDTO(
                id = 1, 
                firstName = "First1", 
                lastName = "Last1", 
                username = "user1", 
                profilePicturePath = null, 
                birthDate = LocalDate.of(1990, 1, 1), 
                country = "USA"
            ),
            UserDTO(
                id = 2, 
                firstName = "First2", 
                lastName = "Last2", 
                username = "user2", 
                profilePicturePath = "path/to/picture.jpg", 
                birthDate = LocalDate.of(1992, 2, 2), 
                country = "Canada"
            )
        )

        coEvery { userService.getAllUsers() } returns users

        application {
            configureTestApplication()
        }

        val token = createTestToken(1, true, email = "test@yahoo.com", credentialId = 1)

        val response = client.get("/user/all") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }

        assertEquals(HttpStatusCode.OK, response.status)

        val expectedJson = Json.encodeToJsonElement(users)
        val actualJson = Json.parseToJsonElement(response.bodyAsText())

        assertEquals(expectedJson, actualJson)

        coVerify(exactly = 1) { userService.getAllUsers() }
    }

    @Test
    fun `GET by-username returns 404 when username is missing`() = testApplication {
        application {
            configureTestApplication()
        }

        val token = createTestToken(userId = 1, email = "test@yahoo.com", credentialId = 1)

        val response = client.get("/user/by-username/") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }

        assertEquals(HttpStatusCode.NotFound, response.status)

        // No response body to check for 404
    }

    @Test
    fun `GET by-username returns 200 with matching users`() = testApplication {
        val username = "test"
        val users = listOf(
            UserDTO(
                id = 1, 
                firstName = "Test", 
                lastName = "User", 
                username = "test", 
                profilePicturePath = null, 
                birthDate = LocalDate.of(1990, 1, 1), 
                country = "USA"
            ),
            UserDTO(
                id = 2, 
                firstName = "Tester", 
                lastName = "User", 
                username = "tester", 
                profilePicturePath = null, 
                birthDate = LocalDate.of(1992, 2, 2), 
                country = "Canada"
            )
        )

        coEvery { userService.getUserByUsername(username) } returns users

        application {
            configureTestApplication()
        }

        val token = createTestToken(userId = 1, email = "test@yahoo.com", credentialId = 1)

        val response = client.get("/user/by-username/$username") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }

        assertEquals(HttpStatusCode.OK, response.status)

        val expectedJson = Json.encodeToJsonElement(users)
        val actualJson = Json.parseToJsonElement(response.bodyAsText())

        assertEquals(expectedJson, actualJson)

        coVerify(exactly = 1) { userService.getUserByUsername(username) }
    }

    @Test
    fun `POST user returns 201 when user created successfully`() = testApplication {
        val request = CreateUserRequest(
            username = "newuser",
            firstName = "New",
            lastName = "User",
            birthDate = LocalDate.of(1990, 1, 1),
            country = "USA"
        )

        coEvery { userService.createUser(any()) } returns 1
        coEvery { jwtService.generateJwtToken(any(), any(), any(), any()) } returns mapOf("token" to "mocked.jwt.token")

        application {
            configureTestApplication()
        }

        val token = createTestToken(userId = 1, email = "test@yahoo.com", credentialId = 1)

        val response = client.post("/user") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(CreateUserRequest.serializer(), request))
            header(HttpHeaders.Authorization, "Bearer $token")
        }

        assertEquals(HttpStatusCode.Created, response.status)

        val expectedJson = Json.parseToJsonElement("""{"token":{"token":"mocked.jwt.token"}}""").jsonObject
        val actualJson = Json.parseToJsonElement(response.bodyAsText()).jsonObject

        assertEquals(expectedJson, actualJson)

        coVerify(exactly = 1) { userService.createUser(any()) }
    }

    @Test
    fun `POST user returns 500 when user creation fails`() = testApplication {
        val request = CreateUserRequest(
            username = "newuser",
            firstName = "New",
            lastName = "User",
            birthDate = LocalDate.of(1990, 1, 1),
            country = "USA"
        )

        coEvery { userService.createUser(any()) } returns -1

        application {
            configureTestApplication()
        }

        val token = createTestToken(userId = 1, email = "test@yahoo.com", credentialId = 1)

        val response = client.post("/user") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(CreateUserRequest.serializer(), request))
            header(HttpHeaders.Authorization, "Bearer $token")
        }

        assertEquals(HttpStatusCode.InternalServerError, response.status)

        val expectedJson = Json.parseToJsonElement("""{"error":"Failed to create user"}""").jsonObject
        val actualJson = Json.parseToJsonElement(response.bodyAsText()).jsonObject

        assertEquals(expectedJson, actualJson)

        coVerify(exactly = 1) { userService.createUser(any()) }
    }

    @Test
    fun `PUT profile-picture returns 401 when no jwt provided`() = testApplication {
        application {
            configureTestApplication()
        }

        val request = UpdateProfilePictureRequest(imagePath = "path/to/new/picture.jpg")

        val response = client.put("/user/profile-picture") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(UpdateProfilePictureRequest.serializer(), request))
        }

        assertEquals(HttpStatusCode.Unauthorized, response.status)

        val expectedJson = Json.parseToJsonElement("""{"error":"Invalid or missing JWT token"}""").jsonObject
        val actualJson = Json.parseToJsonElement(response.bodyAsText()).jsonObject

        assertEquals(expectedJson, actualJson)
    }

    @Test
    fun `PUT profile-picture returns 404 when user not found`() = testApplication {
        val userId = 1
        val request = UpdateProfilePictureRequest(imagePath = "path/to/new/picture.jpg")

        coEvery { userService.updateProfilePicture(userId, request.imagePath) } returns 0

        application {
            configureTestApplication()
        }

        val token = createTestToken(userId = userId, email = "test@yahoo.com", credentialId = 1)

        val response = client.put("/user/profile-picture") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(UpdateProfilePictureRequest.serializer(), request))
            header(HttpHeaders.Authorization, "Bearer $token")
        }

        assertEquals(HttpStatusCode.NotFound, response.status)

        val expectedJson = Json.parseToJsonElement("""{"error":"User not found"}""").jsonObject
        val actualJson = Json.parseToJsonElement(response.bodyAsText()).jsonObject

        assertEquals(expectedJson, actualJson)

        coVerify(exactly = 1) { userService.updateProfilePicture(userId, request.imagePath) }
    }

    @Test
    fun `PUT profile-picture returns 200 when update successful`() = testApplication {
        val userId = 1
        val request = UpdateProfilePictureRequest(imagePath = "path/to/new/picture.jpg")

        coEvery { userService.updateProfilePicture(userId, request.imagePath) } returns 1

        application {
            configureTestApplication()
        }

        val token = createTestToken(userId = userId, email = "test@yahoo.com", credentialId = 1)

        val response = client.put("/user/profile-picture") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(UpdateProfilePictureRequest.serializer(), request))
            header(HttpHeaders.Authorization, "Bearer $token")
        }

        assertEquals(HttpStatusCode.OK, response.status)

        val expectedJson = Json.parseToJsonElement("""{"error":"Profile picture updated"}""").jsonObject
        val actualJson = Json.parseToJsonElement(response.bodyAsText()).jsonObject

        assertEquals(expectedJson, actualJson)

        coVerify(exactly = 1) { userService.updateProfilePicture(userId, request.imagePath) }
    }

    @Test
    fun `DELETE me returns 401 when no jwt provided`() = testApplication {
        application {
            configureTestApplication()
        }

        val response = client.delete("/user/me")

        assertEquals(HttpStatusCode.Unauthorized, response.status)

        val expectedJson = Json.parseToJsonElement("""{"error":"Invalid or missing JWT token"}""").jsonObject
        val actualJson = Json.parseToJsonElement(response.bodyAsText()).jsonObject

        assertEquals(expectedJson, actualJson)
    }

    @Test
    fun `DELETE me returns 404 when user not found`() = testApplication {
        val userId = 1

        coEvery { userService.deleteUser(userId) } returns 0

        application {
            configureTestApplication()
        }

        val token = createTestToken(userId, email = "test@yahoo.com", credentialId = 1)

        val response = client.delete("/user/me") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }

        assertEquals(HttpStatusCode.NotFound, response.status)

        val expectedJson = Json.parseToJsonElement("""{"error":"User not found"}""").jsonObject
        val actualJson = Json.parseToJsonElement(response.bodyAsText()).jsonObject

        assertEquals(expectedJson, actualJson)

        coVerify(exactly = 1) { userService.deleteUser(userId) }
    }

    @Test
    fun `DELETE me returns 200 when user deleted successfully`() = testApplication {
        val userId = 1

        coEvery { userService.deleteUser(userId) } returns 1

        application {
            configureTestApplication()
        }

        val token = createTestToken(userId = userId, email = "test@yahoo.com", credentialId = 1)

        val response = client.delete("/user/me") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }

        assertEquals(HttpStatusCode.OK, response.status)

        val expectedJson = Json.parseToJsonElement("""{"message":"User deleted"}""").jsonObject
        val actualJson = Json.parseToJsonElement(response.bodyAsText()).jsonObject

        assertEquals(expectedJson, actualJson)

        coVerify(exactly = 1) { userService.deleteUser(userId) }
    }

    private fun createTestToken(userId: Int, isAdmin: Boolean = false, email: String, credentialId: Int): String {
        return JWT.create()
            .withClaim("credentialId", credentialId)
            .withClaim("userId", userId)
            .withClaim("isAdmin", isAdmin)
            .withClaim("email", email)
            .withExpiresAt(Date(System.currentTimeMillis() + 60000))
            .sign(Algorithm.HMAC256(System.getenv("JWT_SECRET") ?: "test-secret-key"))
    }
}
